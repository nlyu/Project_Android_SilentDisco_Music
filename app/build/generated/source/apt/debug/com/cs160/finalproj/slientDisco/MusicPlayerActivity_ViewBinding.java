// Generated code from Butter Knife. Do not modify!
package com.cs160.finalproj.slientDisco;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MusicPlayerActivity_ViewBinding implements Unbinder {
  private MusicPlayerActivity target;

  private View view7f0800b9;

  private View view7f0800b8;

  private View view7f0800a4;

  @UiThread
  public MusicPlayerActivity_ViewBinding(MusicPlayerActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MusicPlayerActivity_ViewBinding(final MusicPlayerActivity target, View source) {
    this.target = target;

    View view;
    target.trackCover = Utils.findRequiredViewAsType(source, R.id.track_cover, "field 'trackCover'", ImageView.class);
    view = Utils.findRequiredView(source, R.id.previous_track, "field 'previousTrack' and method 'onPreviousTrackClick'");
    target.previousTrack = Utils.castView(view, R.id.previous_track, "field 'previousTrack'", ImageView.class);
    view7f0800b9 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onPreviousTrackClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.play_or_pause, "field 'playOrPause' and method 'onPlayPauseClick'");
    target.playOrPause = Utils.castView(view, R.id.play_or_pause, "field 'playOrPause'", ImageView.class);
    view7f0800b8 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onPlayPauseClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.next_track, "field 'nextTrack' and method 'onNextTrackClick'");
    target.nextTrack = Utils.castView(view, R.id.next_track, "field 'nextTrack'", ImageView.class);
    view7f0800a4 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onNextTrackClick();
      }
    });
    target.helpIcon = Utils.findRequiredViewAsType(source, R.id.music_party_help_icon, "field 'helpIcon'", ImageView.class);
    target.userIcon = Utils.findRequiredViewAsType(source, R.id.music_party_account_icon, "field 'userIcon'", ImageView.class);
    target.slideUpPanel = Utils.findRequiredView(source, R.id.SlideUpPanel, "field 'slideUpPanel'");
    target.trackTitle = Utils.findRequiredViewAsType(source, R.id.track_title, "field 'trackTitle'", TextView.class);
    target.trackProgress = Utils.findRequiredViewAsType(source, R.id.track_progress, "field 'trackProgress'", TextView.class);
    target.trackDuration = Utils.findRequiredViewAsType(source, R.id.track_duration, "field 'trackDuration'", TextView.class);
    target.trackTimeline = Utils.findRequiredViewAsType(source, R.id.track_timeline, "field 'trackTimeline'", SeekBar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MusicPlayerActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.trackCover = null;
    target.previousTrack = null;
    target.playOrPause = null;
    target.nextTrack = null;
    target.helpIcon = null;
    target.userIcon = null;
    target.slideUpPanel = null;
    target.trackTitle = null;
    target.trackProgress = null;
    target.trackDuration = null;
    target.trackTimeline = null;

    view7f0800b9.setOnClickListener(null);
    view7f0800b9 = null;
    view7f0800b8.setOnClickListener(null);
    view7f0800b8 = null;
    view7f0800a4.setOnClickListener(null);
    view7f0800a4 = null;
  }
}
