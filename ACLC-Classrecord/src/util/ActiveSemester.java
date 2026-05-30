package util;

import dao.SemesterDao;

public final class ActiveSemester {

    private static int id = -1;

    private ActiveSemester() {
    }

    public static int getId() {
        if (id == -1) {
            id = new SemesterDao().getActiveId();
        }
        return id;
    }

    public static void setId(int semesterId) {
        id = semesterId;
    }
}
