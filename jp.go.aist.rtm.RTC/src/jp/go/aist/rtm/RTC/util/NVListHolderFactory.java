package jp.go.aist.rtm.RTC.util;

import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;

/**
 * {@.ja NVListHolder�̃t�@�N�g��}
 * {@.en Implementation of NVListHolder factory}
 */
public class NVListHolderFactory {

    /**
     * {@.ja NVListHolder�𐶐�����}
     * {@.en Creates NVListHolder}
     * 
     * @return 
     *   {@.ja �������ꂽNVListHolder�I�u�W�F�N�g}
     *   {@.en Created NVListHolder object}
     */
    public static NVListHolder create() {
        
        return new NVListHolder(new NameValue[0]);
    }
    
    /**
     * {@.ja NVListHolder�̕����𐶐�����B}
     * {@.en Creates the clone of NVListHolder}
     * 
     * @param rhs 
     *   {@.ja NVListHolder�I�u�W�F�N�g}
     *   {@.en NVListHolder object}
     * @return 
     *   {@.ja �R�s�[���ꂽNVListHolder�I�u�W�F�N�g}
     *   {@.en Copied NVListHolder object}
     */
    public static NVListHolder clone(final NVListHolder rhs) {
        
        NameValue[] value = new NameValue[rhs.value.length];
        for (int i = 0; i < value.length; i++) {
            value[i] = NameValueFactory.clone(rhs.value[i]);
        }
        
        return new NVListHolder(value);
    }
}
