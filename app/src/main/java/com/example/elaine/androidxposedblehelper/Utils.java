package com.example.elaine.androidxposedblehelper;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import static com.example.elaine.androidxposedblehelper.ConstantValue.TAG;

public class Utils {

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void parseBLEInfo(BluetoothGatt gatt) {
        if (gatt == null) {
            return;
        }
        for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
            Log.e(TAG, "BluetoothGattService:" + bluetoothGattService.getUuid().toString());
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                Log.e(TAG, "\tGattCharacteristic:" + bluetoothGattCharacteristic.getUuid().toString());
                for (BluetoothGattDescriptor gattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                    Log.e(TAG, "\t\tDescriptor:" + gattDescriptor.getUuid().toString());
                }
            }
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) {
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }
}
