package taskmanager.enums;

public enum TaskPriorityEnum {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");

    public final String value;

    private TaskPriorityEnum(String label) {
        this.value = label;
    }

    public static boolean isItARightValue(String key) {
        for (TaskPriorityEnum e : TaskPriorityEnum.values()) {
            if (e.value.equals(key)) return true;
        }
        return false;
    }
}
