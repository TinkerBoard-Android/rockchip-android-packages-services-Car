// Copyright (C) 2019 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include "RunnerComponent.h"

#include "ClientConfig.pb.h"
#include "types/Status.h"

namespace android {
namespace automotive {
namespace computepipe {
namespace runner {

/* Is this a notification to enter the phase */
bool RunnerEvent::isPhaseEntry() const {
    return false;
}
/* Is this a notification that all components have transitioned to the phase */
bool RunnerEvent::isTransitionComplete() const {
    return false;
}

bool RunnerEvent::isAborted() const {
    return false;
}

/**
 * ClientConfig methods
 */
Status ClientConfig::dispatchToComponent(const std::shared_ptr<RunnerComponentInterface>& iface) {
    return iface->handleConfigPhase(*this);
}

std::string ClientConfig::getSerializedClientConfig() const {
    proto::ClientConfig config;
    std::string output;

    config.set_input_stream_id(inputStreamId);
    config.set_termination_id(terminationId);
    config.set_offload_id(offloadId);
    for (auto it : outputConfigs) {
        (*config.mutable_output_options())[it.first] = it.second;
    }
    if (!config.SerializeToString(&output)) {
        return "";
    }
    return output;
}

Status ClientConfig::getInputStreamId(int* outId) const {
    if (inputStreamId == kInvalidId) {
        return Status::ILLEGAL_STATE;
    }
    *outId = inputStreamId;
    return Status::SUCCESS;
}

Status ClientConfig::getOffloadId(int* outId) const {
    if (offloadId == kInvalidId) {
        return Status::ILLEGAL_STATE;
    }
    *outId = offloadId;
    return Status::SUCCESS;
}

Status ClientConfig::getTerminationId(int* outId) const {
    if (terminationId == kInvalidId) {
        return Status::ILLEGAL_STATE;
    }
    *outId = terminationId;
    return Status::SUCCESS;
}

Status ClientConfig::getOutputStreamConfigs(std::map<int, int>& outputConfig) const {
    if (outputConfigs.empty()) {
        return Status::ILLEGAL_STATE;
    }
    outputConfig = outputConfigs;
    return Status::SUCCESS;
}

Status ClientConfig::getOptionalConfigs(std::string& outOptional) const {
    outOptional = optionalConfigs;
    return Status::SUCCESS;
}

/**
 * Methods for ComponentInterface
 */

/* handle a ConfigPhase related event notification from Runner Engine */
Status RunnerComponentInterface::handleConfigPhase(const ClientConfig& /* e*/) {
    return Status::SUCCESS;
}
/* handle execution phase notification from Runner Engine */
Status RunnerComponentInterface::handleExecutionPhase(const RunnerEvent& /* e*/) {
    return SUCCESS;
}
/* handle a stop with flushing semantics phase notification from the engine */
Status RunnerComponentInterface::handleStopWithFlushPhase(const RunnerEvent& /* e*/) {
    return SUCCESS;
}
/* handle an immediate stop phase notification from the engine */
Status RunnerComponentInterface::handleStopImmediatePhase(const RunnerEvent& /* e*/) {
    return SUCCESS;
}
/* handle an engine notification to return to reset state */
Status RunnerComponentInterface::handleResetPhase(const RunnerEvent& /* e*/) {
    return SUCCESS;
}

}  // namespace runner
}  // namespace computepipe
}  // namespace automotive
}  // namespace android
