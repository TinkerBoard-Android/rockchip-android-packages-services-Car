/*
 * Copyright (C) 2017 The Android Open Source Project
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

package android.car.vms;

import android.car.vms.VmsLayer;
import android.car.vms.VmsLayersOffering;

/**
 * Exposes publisher services to VMS clients.
 *
 * @hide
 */
interface IVmsPublisherService {
    /**
     * Client call to publish a message.
     */
    oneway void publish(in IBinder token, int layer, int version, in byte[] message) = 0;

    /**
     * Returns whether the layer/version has any clients subscribed to it.
     */
    List<VmsLayer> getSubscriptions() = 1;

    /**
     * Sets which layers the publisher can publish under which dependencties.
     */
    oneway void setLayersOffering(in IBinder token, in VmsLayersOffering offering) = 2;
}
