package com.example.demo.Dto;

import java.time.LocalDateTime;

public class SuspendRequest {

	private LocalDateTime suspendedUntil;

	public LocalDateTime getSuspendedUntil() {
		return suspendedUntil;
	}

	public void setSuspendedUntil(LocalDateTime suspendedUntil) {
		this.suspendedUntil = suspendedUntil;
	}
}
