package com.example.expense_tracker.dto.request;

public class UpdateUserStatusRequest {

    private Boolean active;

    public UpdateUserStatusRequest() {}

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
