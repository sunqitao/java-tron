package org.tron.core.actuator;

import com.google.common.base.Preconditions;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.core.capsule.WitnessCapsule;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Contract.WitnessCreateContract;

public class WitnessCreateActuator extends AbstractActuator {


  private static final Logger logger = LoggerFactory.getLogger("WitnessCreateActuator");

  WitnessCreateActuator(final Any contract, final Manager dbManager) {
    super(contract, dbManager);
  }


  @Override
  public boolean execute() throws ContractExeException {
    try {
      final WitnessCreateContract witnessCreateContract = this.contract
          .unpack(WitnessCreateContract.class);
      this.createWitness(witnessCreateContract);
    } catch (final InvalidProtocolBufferException e) {
      e.printStackTrace();
      throw new ContractExeException(e.getMessage());
    }
    return true;
  }

  @Override
  public boolean validate() throws ContractValidateException {
    try {
      if (!this.contract.is(WitnessCreateContract.class)) {
        throw new ContractValidateException(
            "contract type error,expected type [AccountCreateContract],real type[" + this.contract
                .getClass() + "]");
      }

      final WitnessCreateContract contract = this.contract.unpack(WitnessCreateContract.class);

      Preconditions.checkNotNull(contract.getOwnerAddress(), "OwnerAddress is null");

      if (this.dbManager.getWitnessStore().getWitness(contract.getOwnerAddress()) != null) {
        throw new ContractValidateException("Witness has existed");
      }
    } catch (final Exception ex) {
      ex.printStackTrace();
      throw new ContractValidateException(ex.getMessage());
    }
    return true;
  }

  @Override
  public ByteString getOwnerAddress() throws InvalidProtocolBufferException {
    return contract.unpack(WitnessCreateContract.class).getOwnerAddress();
  }

  @Override
  public long calcFee() {
    return 0;
  }

  private void createWitness(final WitnessCreateContract witnessCreateContract) {
    //Create Witness by witnessCreateContract
    final WitnessCapsule witnessCapsule = new WitnessCapsule(
        witnessCreateContract.getOwnerAddress(), 0, "");

    this.dbManager.getWitnessStore().putWitness(witnessCapsule);
  }

}
