package fr.kevpdev.dicom_processor.entity;

public enum EViewType {
    PARTIAL("Partial View"),
    FULL("Full View"),
    UNKNOWN("Unknown");

    private final String name;

    EViewType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EViewType fromName(String name) {
        for (EViewType e : EViewType.values()) {
            if (e.name.equals(name)) {
                return e;
            }
        }
       return null;
    }
}
