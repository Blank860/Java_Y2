package sdams.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import sdams.model.UserAccount;
import sdams.util.FileHandler;

public class UserService {
    private static final String FILE = "users.txt";

    // Which roles the Administrator may manage
    public static final List<String> STAFF_ROLES = List.of("Instructor", "FacilitiesOfficer", "AttendanceOfficer");
    public static final List<String> STUDENT_ROLES = List.of("Student");

    public List<UserAccount> loadAll() throws IOException {
        List<UserAccount> list = new ArrayList<>();
        for (String line : FileHandler.readLines(FILE)) {
            UserAccount u = UserAccount.fromFileString(line);
            if (u != null) list.add(u);
        }
        return list;
    }

    private void saveAll(List<UserAccount> list) throws IOException {
        List<String> lines = new ArrayList<>();
        for (UserAccount u : list) lines.add(u.toFileString());
        FileHandler.writeLines(FILE, lines);
    }

    /** Matches id, name, email, phone, role or status. Only returns users whose role is in 'roles'. */
    public List<UserAccount> search(String keyword, List<String> roles) throws IOException {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        List<UserAccount> result = new ArrayList<>();
        for (UserAccount u : loadAll()) {
            if (!roles.contains(u.getRole())) continue;
            if (k.isEmpty()
                    || u.getUserId().toLowerCase().contains(k)
                    || u.getFullName().toLowerCase().contains(k)
                    || u.getEmail().toLowerCase().contains(k)
                    || u.getPhone().toLowerCase().contains(k)
                    || u.getRole().toLowerCase().contains(k)
                    || u.getStatus().toLowerCase().contains(k)) {
                result.add(u);
            }
        }
        return result;
    }

    public UserAccount findById(String id) throws IOException {
        for (UserAccount u : loadAll()) if (u.getUserId().equals(id)) return u;
        return null;
    }

    private static String prefixFor(String role) {
        switch (role) {
            case "Manager": return "MGR";
            case "Administrator": return "ADM";
            case "Instructor": return "INS";
            case "Student": return "STU";
            case "FacilitiesOfficer": return "FAC";
            case "AttendanceOfficer": return "ATT";
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    /** Next free ID for a role, e.g. STU003. */
    public String nextId(String role) throws IOException {
        String prefix = prefixFor(role);
        int max = 0;
        for (UserAccount u : loadAll()) {
            String id = u.getUserId();
            if (id.startsWith(prefix)) {
                try { max = Math.max(max, Integer.parseInt(id.substring(prefix.length()))); }
                catch (NumberFormatException ignored) { }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }

    private boolean emailUsedByOther(String email, String ownId) throws IOException {
        for (UserAccount u : loadAll()) {
            if (u.getEmail().equalsIgnoreCase(email) && !u.getUserId().equals(ownId)) return true;
        }
        return false;
    }

    public void add(UserAccount account) throws IOException {
        if (emailUsedByOther(account.getEmail(), account.getUserId())) {
            throw new IllegalArgumentException("That email is already used by another account.");
        }
        List<UserAccount> list = loadAll();
        list.add(account);
        saveAll(list);
    }

    public void update(UserAccount updated) throws IOException {
        if (emailUsedByOther(updated.getEmail(), updated.getUserId())) {
            throw new IllegalArgumentException("That email is already used by another account.");
        }
        List<UserAccount> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserId().equals(updated.getUserId())) {
                list.set(i, updated);
                break;
            }
        }
        saveAll(list);
    }

    /** Deletes only if the account's role is in allowedRoles (so staff screen can't delete students etc.). */
    public void delete(String id, List<String> allowedRoles) throws IOException {
        UserAccount u = findById(id);
        if (u == null) throw new IllegalArgumentException("Account " + id + " not found.");
        if (!allowedRoles.contains(u.getRole())) {
            throw new IllegalArgumentException("You are not allowed to delete this account type.");
        }
        List<UserAccount> list = loadAll();
        list.removeIf(x -> x.getUserId().equals(id));
        saveAll(list);
    }
}