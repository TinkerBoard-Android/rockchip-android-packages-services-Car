/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.experimentalcar;

import static org.mockito.AdditionalMatchers.geq;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.car.experimental.DriverAwarenessEvent;
import android.car.occupantawareness.GazeDetection;
import android.car.occupantawareness.OccupantAwarenessDetection;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GazeDriverAwarenessSupplierTest {

    private static final long FRAME_TIME_MILLIS = 1000L;

    private Context mSpyContext;
    private GazeDriverAwarenessSupplier mGazeSupplier;
    private float mInitialValue;
    private float mGrowthRate;
    private float mDecayRate;

    @Before
    public void setUp() throws Exception {
        mSpyContext = spy(InstrumentationRegistry.getInstrumentation().getTargetContext());
        mGazeSupplier = spy(new GazeDriverAwarenessSupplier(mSpyContext));

        mInitialValue =
                mSpyContext
                        .getResources()
                        .getFloat(R.fraction.driverAwarenessGazeModelInitialValue);
        mGrowthRate =
                mSpyContext.getResources().getFloat(R.fraction.driverAwarenessGazeModelGrowthRate);
        mDecayRate =
                mSpyContext.getResources().getFloat(R.fraction.driverAwarenessGazeModelDecayRate);
    }

    @Test
    public void testonReady_initialCallbackIsGenerated() throws Exception {
        // Supplier should return an initial callback after onReady().
        mGazeSupplier.onReady();

        verify(mGazeSupplier).emitAwarenessEvent(new DriverAwarenessEvent(any(), mInitialValue));
    }

    @Test
    public void testprocessDetectionEvent_noGazeDataProvided() throws Exception {
        // If detection events happen with *no gaze*, no further events should be generated by the
        // attention supplier.
        mGazeSupplier.onReady();

        mGazeSupplier.processDetectionEvent(buildEmptyDetection(0));

        // Should have exactly one call from the initial onReady(), but no further events.
        verify(mGazeSupplier, times(1)).emitAwarenessEvent(new DriverAwarenessEvent(any(), any()));
    }

    @Test
    public void testprocessDetectionEvent_neverExceedsOne() throws Exception {
        // Attention value should never exceed '1' no matter how long the driver looks on-road.
        mGazeSupplier.onReady();

        long timestamp = 0;

        for (int i = 0; i < 100; i++) {
            OccupantAwarenessDetection detection =
                    buildGazeDetection(timestamp, GazeDetection.VEHICLE_REGION_FORWARD_ROADWAY);
            mGazeSupplier.processDetectionEvent(detection);

            verify(mGazeSupplier).emitAwarenessEvent(new DriverAwarenessEvent(any(), leq(1)));

            timestamp += FRAME_TIME_MILLIS;
        }
    }

    @Test
    public void testprocessDetectionEvent_neverFallsBelowZero() throws Exception {
        // Attention value should never fall below '0' no matter how long the driver looks off-road.
        mGazeSupplier.onReady();

        long timestamp = 0;

        for (int i = 0; i < 100; i++) {
            OccupantAwarenessDetection detection =
                    buildGazeDetection(timestamp, GazeDetection.VEHICLE_REGION_HEAD_UNIT_DISPLAY);
            mGazeSupplier.processDetectionEvent(detection);

            verify(mGazeSupplier).emitAwarenessEvent(new DriverAwarenessEvent(any(), geq(0)));

            timestamp += FRAME_TIME_MILLIS;
        }
    }

    /** Builds a {link OccupantAwarenessDetection} with the specified target for testing. */
    private OccupantAwarenessDetection buildGazeDetection(
            long timestamp, @GazeDetection.VehicleRegion int gazeTarget) {
        GazeDetection gaze =
                new GazeDetection(
                        OccupantAwarenessDetection.CONFIDENCE_LEVEL_HIGH,
                        null /*leftEyePosition*/,
                        null /*rightEyePosition*/,
                        null /*headAngleUnitVector*/,
                        null /*gazeAngleUnitVector*/,
                        gazeTarget,
                        FRAME_TIME_MILLIS);

        return new OccupantAwarenessDetection(
                OccupantAwarenessDetection.VEHICLE_OCCUPANT_DRIVER, timestamp, true, gaze, null);
    }

    /** Builds a {link OccupantAwarenessDetection} with the specified target for testing. */
    private OccupantAwarenessDetection buildEmptyDetection(long timestamp) {
        return new OccupantAwarenessDetection(
                OccupantAwarenessDetection.VEHICLE_OCCUPANT_DRIVER, timestamp, true, null, null);
    }
}