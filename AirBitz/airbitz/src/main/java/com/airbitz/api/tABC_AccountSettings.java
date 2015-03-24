/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.airbitz.api;

public class tABC_AccountSettings {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected tABC_AccountSettings(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(tABC_AccountSettings obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        coreJNI.delete_tABC_AccountSettings(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setSzFirstName(String value) {
    coreJNI.tABC_AccountSettings_szFirstName_set(swigCPtr, this, value);
  }

  public String getSzFirstName() {
    return coreJNI.tABC_AccountSettings_szFirstName_get(swigCPtr, this);
  }

  public void setSzLastName(String value) {
    coreJNI.tABC_AccountSettings_szLastName_set(swigCPtr, this, value);
  }

  public String getSzLastName() {
    return coreJNI.tABC_AccountSettings_szLastName_get(swigCPtr, this);
  }

  public void setSzNickname(String value) {
    coreJNI.tABC_AccountSettings_szNickname_set(swigCPtr, this, value);
  }

  public String getSzNickname() {
    return coreJNI.tABC_AccountSettings_szNickname_get(swigCPtr, this);
  }

  public void setSzPIN(String value) {
    coreJNI.tABC_AccountSettings_szPIN_set(swigCPtr, this, value);
  }

  public String getSzPIN() {
    return coreJNI.tABC_AccountSettings_szPIN_get(swigCPtr, this);
  }

  public void setBNameOnPayments(boolean value) {
    coreJNI.tABC_AccountSettings_bNameOnPayments_set(swigCPtr, this, value);
  }

  public boolean getBNameOnPayments() {
    return coreJNI.tABC_AccountSettings_bNameOnPayments_get(swigCPtr, this);
  }

  public void setMinutesAutoLogout(int value) {
    coreJNI.tABC_AccountSettings_minutesAutoLogout_set(swigCPtr, this, value);
  }

  public int getMinutesAutoLogout() {
    return coreJNI.tABC_AccountSettings_minutesAutoLogout_get(swigCPtr, this);
  }

  public void setRecoveryReminderCount(int value) {
    coreJNI.tABC_AccountSettings_recoveryReminderCount_set(swigCPtr, this, value);
  }

  public int getRecoveryReminderCount() {
    return coreJNI.tABC_AccountSettings_recoveryReminderCount_get(swigCPtr, this);
  }

  public void setSzLanguage(String value) {
    coreJNI.tABC_AccountSettings_szLanguage_set(swigCPtr, this, value);
  }

  public String getSzLanguage() {
    return coreJNI.tABC_AccountSettings_szLanguage_get(swigCPtr, this);
  }

  public void setCurrencyNum(int value) {
    coreJNI.tABC_AccountSettings_currencyNum_set(swigCPtr, this, value);
  }

  public int getCurrencyNum() {
    return coreJNI.tABC_AccountSettings_currencyNum_get(swigCPtr, this);
  }

  public void setExchangeRateSources(tABC_ExchangeRateSources value) {
    coreJNI.tABC_AccountSettings_exchangeRateSources_set(swigCPtr, this, tABC_ExchangeRateSources.getCPtr(value), value);
  }

  public tABC_ExchangeRateSources getExchangeRateSources() {
    long cPtr = coreJNI.tABC_AccountSettings_exchangeRateSources_get(swigCPtr, this);
    return (cPtr == 0) ? null : new tABC_ExchangeRateSources(cPtr, false);
  }

  public void setBitcoinDenomination(tABC_BitcoinDenomination value) {
    coreJNI.tABC_AccountSettings_bitcoinDenomination_set(swigCPtr, this, tABC_BitcoinDenomination.getCPtr(value), value);
  }

  public tABC_BitcoinDenomination getBitcoinDenomination() {
    long cPtr = coreJNI.tABC_AccountSettings_bitcoinDenomination_get(swigCPtr, this);
    return (cPtr == 0) ? null : new tABC_BitcoinDenomination(cPtr, false);
  }

  public void setBAdvancedFeatures(boolean value) {
    coreJNI.tABC_AccountSettings_bAdvancedFeatures_set(swigCPtr, this, value);
  }

  public boolean getBAdvancedFeatures() {
    return coreJNI.tABC_AccountSettings_bAdvancedFeatures_get(swigCPtr, this);
  }

  public void setSzFullName(String value) {
    coreJNI.tABC_AccountSettings_szFullName_set(swigCPtr, this, value);
  }

  public String getSzFullName() {
    return coreJNI.tABC_AccountSettings_szFullName_get(swigCPtr, this);
  }

  public void setBDailySpendLimit(boolean value) {
    coreJNI.tABC_AccountSettings_bDailySpendLimit_set(swigCPtr, this, value);
  }

  public boolean getBDailySpendLimit() {
    return coreJNI.tABC_AccountSettings_bDailySpendLimit_get(swigCPtr, this);
  }

  public void setDailySpendLimitSatoshis(SWIGTYPE_p_int64_t value) {
    coreJNI.tABC_AccountSettings_dailySpendLimitSatoshis_set(swigCPtr, this, SWIGTYPE_p_int64_t.getCPtr(value));
  }

  public SWIGTYPE_p_int64_t getDailySpendLimitSatoshis() {
    return new SWIGTYPE_p_int64_t(coreJNI.tABC_AccountSettings_dailySpendLimitSatoshis_get(swigCPtr, this), true);
  }

  public void setBSpendRequirePin(boolean value) {
    coreJNI.tABC_AccountSettings_bSpendRequirePin_set(swigCPtr, this, value);
  }

  public boolean getBSpendRequirePin() {
    return coreJNI.tABC_AccountSettings_bSpendRequirePin_get(swigCPtr, this);
  }

  public void setSpendRequirePinSatoshis(SWIGTYPE_p_int64_t value) {
    coreJNI.tABC_AccountSettings_spendRequirePinSatoshis_set(swigCPtr, this, SWIGTYPE_p_int64_t.getCPtr(value));
  }

  public SWIGTYPE_p_int64_t getSpendRequirePinSatoshis() {
    return new SWIGTYPE_p_int64_t(coreJNI.tABC_AccountSettings_spendRequirePinSatoshis_get(swigCPtr, this), true);
  }

  public void setBDisablePINLogin(boolean value) {
    coreJNI.tABC_AccountSettings_bDisablePINLogin_set(swigCPtr, this, value);
  }

  public boolean getBDisablePINLogin() {
    return coreJNI.tABC_AccountSettings_bDisablePINLogin_get(swigCPtr, this);
  }

  public void setPinLoginCount(int value) {
    coreJNI.tABC_AccountSettings_pinLoginCount_set(swigCPtr, this, value);
  }

  public int getPinLoginCount() {
    return coreJNI.tABC_AccountSettings_pinLoginCount_get(swigCPtr, this);
  }

  public tABC_AccountSettings() {
    this(coreJNI.new_tABC_AccountSettings(), true);
  }

}
