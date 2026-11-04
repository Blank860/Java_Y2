package sdams.ui;

import sdams.service.UserService;

/** Manage Students: add, view, search, update and delete Student accounts. */
public class StudentPanel extends UserManagementPanel {
    public StudentPanel() {
        super("Student", UserService.STUDENT_ROLES);
    }
}