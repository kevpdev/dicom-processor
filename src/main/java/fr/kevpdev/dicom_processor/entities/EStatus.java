package fr.kevpdev.dicom_processor.entities;

public enum EStatus {
    SUCCESS("SUCCESS"),
    IGNORED("IGNORED"),
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
