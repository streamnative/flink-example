package io.streamnative.flink.java.sql.models;

import java.io.Serializable;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ExampleUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private int age;

    private double income;

    private boolean single;

    private long createTime;

    public ExampleUser(String name, int age, double income, boolean single, long createTime) {
        this.name = name;
        this.age = age;
        this.income = income;
        this.single = single;
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getIncome() {
        return income;
    }

    public boolean isSingle() {
        return single;
    }

    public long getCreateTime() {
        return createTime;
    }

    public static ExampleUser createRandomUserWithCreateTime(long createTime) {
        return new ExampleUser(
            "name" + randomAlphabetic(3),
            10,
            0.1,
            createTime % 2 == 0,
            createTime
        );
    }
}
