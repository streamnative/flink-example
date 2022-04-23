package io.streamnative.flink.java.common;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import io.streamnative.flink.java.common.InfiniteSourceFunction.InfiniteGenerator;
import net.datafaker.Faker;

import java.io.Serializable;
import java.util.Random;

/**
 * Generate an infinite string message.
 */
public class FakerGenerator implements InfiniteGenerator<String>, Serializable {
    private static final long serialVersionUID = -3598419528328743200L;

    private transient Faker faker;
    private int taskId;

    @Override
    public String generate() {
        return taskId + " - " + faker.name().fullName();
    }

    @Override
    public void open(RuntimeContext runtimeContext) {
        this.faker = new Faker(new Random());
        this.taskId = runtimeContext.getIndexOfThisSubtask();
    }

    @Override
    public TypeInformation<String> getType() {
        return Types.STRING;
    }
}
