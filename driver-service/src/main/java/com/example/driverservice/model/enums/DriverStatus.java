package com.example.driverservice.model.enums;

public enum DriverStatus {
    NO_CAR, //чтобы драйвер не мог взять поездку, если у него нет тачки
    AVAILABLE,
    UNAVAILABLE,
    DELETED //чтобы драйвер не мог взять поездку, если он удалён
}
