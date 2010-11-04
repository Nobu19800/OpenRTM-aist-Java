package jp.go.aist.rtm.RTC.util;

import RTC.Time;
import RTC.TimedFloat;

/**
 * {@.ja TimedFloat�̃t�@�N�g��}
 * {@.en Implementation of TimedFloat factory}
 */
public class TimedFloatFactory {

    /**
     * {@.ja TimedFloat�𐶐�����B}
     * {@.en Creates TimedFloat}
     * 
     * @return 
     *   {@.ja �������ꂽTimedFloat�I�u�W�F�N�g}
     *   {@.en Created TimedFloat object}
     */
    public static TimedFloat create() {
        
        TimedFloat tf = new TimedFloat();
        tf.tm = new Time();
        
        return tf;
    }
}
