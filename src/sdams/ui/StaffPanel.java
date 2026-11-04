package sdams.ui;

import sdams.service.UserService;

/** Manage Staff: Instructors, Facilities Officers and Attendance Officers. */
public class StaffPanel extends UserManagementPanel {
    public StaffPanel() {
        super("Staff", UserService.STAFF_ROLES);
    }
}