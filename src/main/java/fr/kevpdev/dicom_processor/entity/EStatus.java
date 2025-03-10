package fr.kevpdev.dicom_processor.entity;

public enum EStatus {
    SUCCESS("SUCCESS"),
    IN_PROGRESS("IN_PROGRESS"),
    ERROR("ERROR");

    private final String name;

    EStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EStatus fromValue(String value) {
        for (EStatus eStatus : EStatus.values()) {
            if (eStatus.name.equals(value)) {
                return eStatus;
            }
        }
        return null;
    }
}
