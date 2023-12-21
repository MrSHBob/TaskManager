package taskmanager.enums;


public enum TaskStatusEnum{
    NEW("New"),
    IN_PROCESS("InProcess"),
    DONE("Done"),
    CANCELED("Canceled");

    public final String value;

    private TaskStatusEnum(String label) {
        this.value = label;
    }

    public static boolean isItARightValue(String key) {
        for (TaskStatusEnum e : TaskStatusEnum.values()) {
            if (e.value.equals(key)) return true;
        }
        return false;
    }

}
