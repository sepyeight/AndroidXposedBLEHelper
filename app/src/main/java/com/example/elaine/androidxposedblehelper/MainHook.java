package com.example.elaine.androidxposedblehelper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
import static android.bluetooth.BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static com.example.elaine.androidxposedblehelper.ConstantValue.TAG;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //1.hook connectGatt
//      //public BluetoothGatt connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback)
        XposedHelpers.findAndHookMethod(BluetoothDevice.class, "connectGatt", Context.class, boolean.class, BluetoothGattCallback.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                BluetoothDevice device = (BluetoothDevice) param.thisObject;
                BluetoothGattCallback callback = (BluetoothGattCallback) param.args[2];
                Log.e(TAG, "BluetoothDevice.connectGatt: device info:name: " + device.getName());
                Log.e(TAG, "BluetoothDevice.connectGatt: device info:MAC: " + device.getAddress());

                if (callback != null) {
                    Log.e(TAG, "BluetoothGattCallback callback class name:" + callback.getClass().getName());
                    //begin
                    XposedHelpers.findAndHookMethod(callback.getClass(), "onCharacteristicRead", BluetoothGatt.class, BluetoothGattCharacteristic.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[1];
                            Log.e(TAG, "BluetoothGattCallback.onCharacteristicRead: characteristic:" + characteristic.getUuid() + ", value:" + Utils.bytesToHex(characteristic.getValue()) + ", status:" + param.args[2]);
                        }
                    });

                    XposedHelpers.findAndHookMethod(callback.getClass(), "onCharacteristicWrite", BluetoothGatt.class, BluetoothGattCharacteristic.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[1];
                            Log.e(TAG, "BluetoothGattCallback.onCharacteristicWrite: characteristic:" + characteristic.getUuid() + ", value:" + Utils.bytesToHex(characteristic.getValue()) + ", status:" + param.args[2]);
                        }
                    });

                    XposedHelpers.findAndHookMethod(callback.getClass(), "onCharacteristicChanged", BluetoothGatt.class, BluetoothGattCharacteristic.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[1];
                            Log.e(TAG, "BluetoothGattCallback.onCharacteristicChanged: characteristic:" + characteristic.getUuid() + ", value:" + Utils.bytesToHex(characteristic.getValue()));
                        }
                    });

                    XposedHelpers.findAndHookMethod(callback.getClass(), "onDescriptorWrite", BluetoothGatt.class, BluetoothGattDescriptor.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            BluetoothGattDescriptor descriptor = (BluetoothGattDescriptor) param.args[1];
                            Log.e(TAG, "BluetoothGattCallback.onDescriptorWrite: descriptor:" + descriptor.getUuid() + ", value:" + Utils.bytesToHex(descriptor.getValue()) + ", status:" + param.args[2]);
                        }
                    });

                    XposedHelpers.findAndHookMethod(callback.getClass(), "onDescriptorRead", BluetoothGatt.class, BluetoothGattDescriptor.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            BluetoothGattDescriptor descriptor = (BluetoothGattDescriptor) param.args[1];
                            Log.e(TAG, "BluetoothGattCallback.onDescriptorWrite: descriptor:" + descriptor.getUuid() + ", value:" + Utils.bytesToHex(descriptor.getValue()) + ", status:" + param.args[2]);
                        }
                    });

                    XposedHelpers.findAndHookMethod(callback.getClass(), "onServicesDiscovered", BluetoothGatt.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            int status = (int) param.args[1];
                            String result = status == 0 ? "GATT_STATUS" : status + "";
                            Log.e(TAG, "BluetoothGattCallback.onServicesDiscovered: status:" + result);
                            BluetoothGatt gatt = (BluetoothGatt) param.args[0];
                            Utils.parseBLEInfo(gatt);
                        }
                    });

                    XposedHelpers.findAndHookMethod(callback.getClass(), "onMtuChanged", BluetoothGatt.class, int.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            int status = (int) param.args[1];
                            String result = status == 0 ? "GATT_STATUS" : status + "";
                            Log.e(TAG, "BluetoothGattCallback.onMtuChanged: onMtuChanged mtu:" + param.args[1] + ", status:" + result);
                        }
                    });

                    XposedHelpers.findAndHookMethod(callback.getClass(), "onConnectionStateChange", BluetoothGatt.class, int.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if (BluetoothProfile.STATE_CONNECTED == (int) param.args[2]) {
                                Log.e(TAG, "BluetoothGattCallback.onConnectionStateChange: connection success");
                            } else {
                                Log.e(TAG, "BluetoothGattCallback.onConnectionStateChange: connection failed");
                            }

                        }
                    });
                    //end
                } else {
                    Log.e(TAG, "BluetoothGattCallback callback: null");
                }
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGatt.class, "setCharacteristicNotification", BluetoothGattCharacteristic.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                boolean flag = (boolean) param.args[1];
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[0];
                Log.e(TAG, "BluetoothGatt.setCharacteristicNotification: characteristic uuid:" + characteristic.getUuid().toString() + ", notify:" + flag);
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGattDescriptor.class, "setValue", byte[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                byte[] value = (byte[]) param.args[0];
                BluetoothGattDescriptor descriptor = (BluetoothGattDescriptor) param.thisObject;
                if (Arrays.equals(value, ENABLE_NOTIFICATION_VALUE)) {
                    Log.e(TAG, "BluetoothGattDescriptor.setValue: descriptor:" + descriptor.getUuid() + " ENABLE_NOTIFICATION_VALUE");
                } else if (Arrays.equals(value, DISABLE_NOTIFICATION_VALUE)) {
                    Log.e(TAG, "BluetoothGattDescriptor.setValue: descriptor:" + descriptor.getUuid() + " DISABLE_NOTIFICATION_VALUE");
                } else if (Arrays.equals(value, ENABLE_INDICATION_VALUE)) {
                    Log.e(TAG, "BluetoothGattDescriptor.setValue: descriptor:" + descriptor.getUuid() + " ENABLE_INDICATION_VALUE");
                } else {
                    Log.e(TAG, "BluetoothGattDescriptor.setValue: descriptor:" + descriptor.getUuid() + ", value:" + Utils.bytesToHex(value));
                }
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGatt.class, "requestMtu", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e(TAG, "BluetoothGatt.requestMtu: set Mtu:" + param.args[0]);
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGattCharacteristic.class, "setValue", byte[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.thisObject;
                byte[] value = (byte[]) param.args[0];
                Log.e(TAG, "BluetoothGattCharacteristic.setValue: characteristic:" + characteristic.getUuid() + ", value bytes:" + Utils.bytesToHex(value));
                try {
                    throw new Exception("lajii");
                } finally {

                }
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGattCharacteristic.class, "setWriteType", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.thisObject;
                int value = (int) param.args[0];
                if (value == WRITE_TYPE_DEFAULT) {
                    Log.e(TAG, "BluetoothGattCharacteristic.setValue: characteristic:" + characteristic.getUuid() + ", writeType WRITE_TYPE_NO_RESPONSE");
                } else if (value == WRITE_TYPE_NO_RESPONSE){
                    Log.e(TAG, "BluetoothGattCharacteristic.setValue: characteristic:" + characteristic.getUuid() + ", writeType WRITE_TYPE_NO_RESPONSE");
                }else {
                    Log.e(TAG, "BluetoothGattCharacteristic.setValue: characteristic:" + characteristic.getUuid() + ", writeType bytes:" + value);
                }
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGattCharacteristic.class, "setValue", int.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.thisObject;
                int value = (int) param.args[0];
                int formatType = (int) param.args[1];
                int offset = (int) param.args[2];
                Log.e(TAG, "BluetoothGattCharacteristic.setValue: characteristic:" + characteristic.getUuid() + ", value:" + value + ", " + formatType + ", " + offset);
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGattCharacteristic.class, "setValue", int.class, int.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.thisObject;
                int mantissa = (int) param.args[0];
                int exponent = (int) param.args[1];
                int formatType = (int) param.args[2];
                int offset = (int) param.args[3];
                Log.e(TAG, "BluetoothGattCharacteristic.setValue: characteristic:" + characteristic.getUuid() + ", value:" + mantissa + ", " + exponent + ", " + formatType + ", " + offset);
            }
        });

        XposedHelpers.findAndHookMethod(BluetoothGattCharacteristic.class, "setValue", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.thisObject;
                String value = (String) param.args[0];
                Log.e(TAG, "BluetoothGattCharacteristic.setValue: characteristic:" + characteristic.getUuid() + ", value string:" + value);
                try {
                    throw new Exception("laji");
                } finally {

                }
            }
        });
    }
}
