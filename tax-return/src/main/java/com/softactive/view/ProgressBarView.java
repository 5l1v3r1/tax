package com.softactive.view;

import java.io.Serializable;

import lombok.Setter;

public class ProgressBarView implements Serializable {
	private static final long serialVersionUID = 7115567419807972088L;
	@Setter
	private Integer progress;
	@Setter
	private int total = 100;
	private int done;

	public ProgressBarView(int total) {
		this.total = total;
		done = 0;
	}

	public Integer getProgress() {
		if (progress == null) {
			progress = 0;
		}
		// else {
		// progress = progress + (int)(Math.random() * 35);
		//// progress = (int)(done*100)/total;
		//// progress += 50;
		// if(progress > 100)
		// progress = 100;
		// }
		System.out.println("done: " + done);
		System.out.println("total: " + total);

		progress = (Integer) ((done * 100) / total);
		System.out.println("progress: " + progress);
		return progress;
	}

	public void progress() {
		done++;
	}

	public void onComplete() {
		done = 0;
	}

	public void cancel() {
		progress = null;
	}
}