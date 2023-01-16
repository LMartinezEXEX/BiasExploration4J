package com.biasexplorer4j;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.biasexplorer4j.WordExploration.Visualization.Visualizer;

public class SetupExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        String uniqueKey = this.getClass().getName();
        Object value = context.getRoot().getStore(GLOBAL).get(uniqueKey);
        if (value == null) {
            context.getRoot().getStore(GLOBAL).put(uniqueKey, this);
            setup();
        }
    }

    @Override
    public void close() throws Exception {
        System.out.println("CLOSING!");
    }

    public void setup() {
        Visualizer.setup();
        System.out.println("SETUP!");
    }
}
