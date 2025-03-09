package fr.kevpdev.dicom_processor.entity;

public enum EStatus {
    SUCCESS("SUCCESS"),
    IN_PROGRESS("IN_PROGRESS"),
    ERROR("ERROR");

    private final String value;

    EStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EStatus fromValue(String value) {
        for (EStatus eStatus : EStatus.values()) {
            if (eStatus.value.equals(value)) {
                return eStatus;
            }
        }
        return null;
    }
}
