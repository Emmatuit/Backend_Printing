package com.example.demo.Dto;

import java.util.List;

public class UserSummaryResponse {

	private long totalUsers;

	private List<UserDto> users;

	public UserSummaryResponse(long totalUsers, List<UserDto> users) {
		this.totalUsers = totalUsers;
		this.users = users;
	}

	public long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public List<UserDto> getUsers() {
		return users;
	}

	public void setUsers(List<UserDto> users) {
		this.users = users;
	}

	// Getters
}
