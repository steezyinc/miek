/*
 * Copyright (C) 2015 Lyft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.steezy.android.imagecropsample;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.squareup.leakcanary.RefWatcher;
import java.io.File;

import co.steezy.android.imagecropsample.databinding.ActivityMainBinding;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class MainActivity extends AppCompatActivity {

    private static final float[] ASPECT_RATIOS = { 0f, 1f, 6f/4f, 16f/9f };

    private static final String[] ASPECT_LABELS = { "\u00D8", "1:1", "6:4", "16:9" };

    private ActivityMainBinding binding;

    CompositeSubscription subscriptions = new CompositeSubscription();

    private int selectedRatio = 0;
    private AnimatorListener animatorListener = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            binding.cropFab.setVisibility(View.INVISIBLE);
            binding.pickMiniFab.setVisibility(View.INVISIBLE);
            binding.ratioFab.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            binding.cropFab.setVisibility(View.VISIBLE);
            binding.pickMiniFab.setVisibility(View.VISIBLE);
            binding.ratioFab.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        binding.cropView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getPointerCount() > 1 || binding.cropView.getImageBitmap() == null) {
                return true;
            }

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    binding.cropFab.setVisibility(View.INVISIBLE);
                    binding.pickMiniFab.setVisibility(View.INVISIBLE);
                    binding.ratioFab.setVisibility(View.INVISIBLE);
                    break;
                default:
                    binding.cropFab.setVisibility(View.VISIBLE);
                    binding.pickMiniFab.setVisibility(View.VISIBLE);
                    binding.ratioFab.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCodes.PICK_IMAGE_FROM_GALLERY
                && resultCode == Activity.RESULT_OK) {
            Uri galleryPictureUri = data.getData();

            binding.cropView.extensions()
                    .load(galleryPictureUri);

            updateButtons();
        }
    }

    public void onCropClicked(View view) {
        final File croppedFile = new File(getCacheDir(), "cropped.jpg");

        Observable<Void> onSave = Observable.from(binding.cropView.extensions()
                .crop()
                .quality(100)
                .format(JPEG)
                .into(croppedFile))
                .subscribeOn(io())
                .observeOn(mainThread());

        subscriptions.add(onSave
                .subscribe(nothing -> CropResultActivity.startUsing(croppedFile, MainActivity.this)));
    }

    public void onPickClicked(View view) {
        binding.cropView.extensions()
                .pickUsing(this, RequestCodes.PICK_IMAGE_FROM_GALLERY);
    }

    public void onRatioClicked(View view) {
        final float oldRatio = binding.cropView.getImageRatio();
        selectedRatio = (selectedRatio + 1) % ASPECT_RATIOS.length;

        // Since the animation needs to interpolate to the native
        // ratio, we need to get that instead of using 0
        float newRatio = ASPECT_RATIOS[selectedRatio];
        if (Float.compare(0, newRatio) == 0) {
            newRatio = binding.cropView.getImageRatio();
        }

        ObjectAnimator viewportRatioAnimator = ObjectAnimator.ofFloat(binding.cropView, "viewportRatio", oldRatio, newRatio)
                .setDuration(420);
        viewportRatioAnimator.setAutoCancel(true);
        viewportRatioAnimator.addListener(animatorListener);
        viewportRatioAnimator.start();

        Toast.makeText(this, ASPECT_LABELS[selectedRatio], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        subscriptions.unsubscribe();

        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this, "MainActivity");
        refWatcher.watch(binding.cropView, "cropView");
    }

    private void updateButtons() {
        binding.cropFab.setVisibility(View.VISIBLE);
        binding.pickMiniFab.setVisibility(View.VISIBLE);
        binding.ratioFab.setVisibility(View.VISIBLE);
        binding.pickFab.setVisibility(View.GONE);
    }
}
