package jp.go.aist.rtm.rtclink.manager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import jp.go.aist.rtm.rtclink.RtcLinkPlugin;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

/**
 * �ݒ���Ǘ�����}�l�[�W��
 * <p>
 * �ݒ���ɃA�N�Z�X����ɂ͂��̃C���X�^���X���g�p����
 */
public class PreferenceManager {
	private static PreferenceManager __instance = new PreferenceManager();

	/**
	 * RTC��ԐF�̃L�[�F Start
	 */
	public static final String COLOR_RTC_STATE_CREATED = PreferenceManager.class
			.getName()
			+ "COLOR_RTC_STATE_CREATED";

	/**
	 * RTC��ԐF�̃L�[�F InActive
	 */
	public static final String COLOR_RTC_STATE_INACTIVE = PreferenceManager.class
			.getName()
			+ "COLOR_RTC_STATE_INACTIVE";

	/**
	 * RTC��ԐF�̃L�[�F Active
	 */
	public static final String COLOR_RTC_STATE_ACTIVE = PreferenceManager.class
			.getName()
			+ "COLOR_RTC_STATE_ACTIVE";

	/**
	 * RTC��ԐF�̃L�[�F Error
	 */
	public static final String COLOR_RTC_STATE_ERROR = PreferenceManager.class
			.getName()
			+ "COLOR_RTC_STATE_ERROR";

	/**
	 * RTC��ԐF�̃L�[�F UnKnown
	 */
	public static final String COLOR_RTC_STATE_UNKNOWN = PreferenceManager.class
			.getName()
			+ "COLOR_RTC_STATE_UNKNOWN";

	/**
	 * RTCExecutionContext�F�̃L�[�F Running
	 */
	public static final String COLOR_RTC_EXECUTION_CONTEXT_RUNNING = PreferenceManager.class
			.getName()
			+ "COLOR_RTC_EXECUTION_CONTEXT_RUNNING";

	/**
	 * RTCExecutionContext�F�̃L�[�F Stopped
	 */
	public static final String COLOR_RTC_EXECUTION_CONTEXT_STOPPED = PreferenceManager.class
			.getName()
			+ "COLOR_RTC_EXECUTION_CONTEXT_STOPPED";

	/**
	 * DataPort�F�̃L�[�F ���ڑ�
	 */
	public static final String COLOR_DATAPORT_NO_CONNECT = PreferenceManager.class
			.getName()
			+ "COLOR_DATAPORT_NO_CONNECT";

	/**
	 * DataPort�F�̃L�[�F �ڑ���
	 */
	public static final String COLOR_DATAPORT_CONNECTED = PreferenceManager.class
			.getName()
			+ "COLOR_DATAPORT_CONNECTED";

	/**
	 * ServicePort�F�̃L�[�F ���ڑ�
	 */
	public static final String COLOR_SERVICEPORT_NO_CONNECT = PreferenceManager.class
			.getName()
			+ "COLOR_SERVICEPORT_NO_CONNECT";

	/**
	 * ServicePort�F�̃L�[�F �ڑ���
	 */
	public static final String COLOR_SERVICEPORT_CONNECTED = PreferenceManager.class
			.getName()
			+ "COLOR_SERVICEPORT_CONNECTED";

	/**
	 * �����Ԋu�̃L�[�F �V�X�e���G�f�B�^
	 */
	public static final String SYNC_SYSTEMEDITOR_INTERVAL = PreferenceManager.class
			.getName()
			+ "SYNC_SYSTEMEDITOR_INTERVAL";

	/**
	 * �����Ԋu�̃L�[�F �l�[���T�[�o
	 */
	public static final String SYNC_NAMESERVER_INTERVAL = PreferenceManager.class
			.getName()
			+ "SYNC_NAMESERVICE_INTERVAL";

	/**
	 * �f�t�H���g�ڑ��|�[�g
	 */
	public static final String DEFAULT_CONNECTION_PORT = PreferenceManager.class
			.getName()
			+ "DEFAULT_CONNECTION_PORT";

	/**
	 * �^�C���A�E�g���莞��
	 */
	public static final String DEFAULT_TIMEOUT_PERIOD = PreferenceManager.class
			.getName()
			+ "DEFAULT_TIMEOUT_PERIOD";

	/**
	 * �f�t�H���g�̐F���Ǘ�����}�b�v
	 */
	public static final Map<String, RGB> defaultRGBMap = new HashMap<String, RGB>();
	static {
		defaultRGBMap.put(COLOR_RTC_STATE_CREATED, new RGB(255, 255, 255));
		defaultRGBMap.put(COLOR_RTC_STATE_INACTIVE, new RGB(0, 0, 255));
		defaultRGBMap.put(COLOR_RTC_STATE_ACTIVE, new RGB(0, 255, 0));
		defaultRGBMap.put(COLOR_RTC_STATE_ERROR, new RGB(255, 0, 0));
		defaultRGBMap.put(COLOR_RTC_STATE_UNKNOWN, new RGB(0, 0, 0));
		defaultRGBMap.put(COLOR_RTC_EXECUTION_CONTEXT_RUNNING, new RGB(128,
				128, 128));
		defaultRGBMap
				.put(COLOR_RTC_EXECUTION_CONTEXT_STOPPED, new RGB(0, 0, 0));
		defaultRGBMap.put(COLOR_DATAPORT_NO_CONNECT, new RGB(0, 0, 255));
		defaultRGBMap.put(COLOR_DATAPORT_CONNECTED, new RGB(96, 255, 96));
		defaultRGBMap.put(COLOR_SERVICEPORT_NO_CONNECT, new RGB(127, 127, 255));
		defaultRGBMap.put(COLOR_SERVICEPORT_CONNECTED, new RGB(0, 255, 255));
	}

	/**
	 * �f�t�H���g�̓����Ԋu���Ǘ�����}�b�v
	 */
	public static final Map<String, Integer> defaultInvervalMap = new HashMap<String, Integer>();
	static {
		defaultInvervalMap.put(SYNC_SYSTEMEDITOR_INTERVAL, 1000);
		defaultInvervalMap.put(SYNC_NAMESERVER_INTERVAL, 1000);
	}

	/**
	 * �f�t�H���g�ڑ��|�[�g
	 */
	public static final String defaultConnectionPort = new String("2809");

	/**
	 * �f�t�H���g�^�C���A�E�g���莞��
	 */
	public static final int defaultTimeoutPeriod = 3000;

	/**
	 * �L���b�V�������F�i���\�[�X�j���Ǘ�����}�b�v
	 */
	private static transient final Map<String, Color> cachedColorMap = new HashMap<String, Color>();

	/**
	 * �R���X�g���N�^
	 * 
	 * @return �V���O���g��
	 */
	public static PreferenceManager getInstance() {
		return __instance;
	}

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/**
	 * �L�[����F��Ԃ�
	 * <p>
	 * �F�͂�\�[�X�ł��邽�߁A�L���b�V�����Ďg�p���Ă���B
	 * 
	 * @param key
	 * @return �F
	 */
	public synchronized Color getColor(String key) {
		RGB rgb = getRGB(key);

		Color result = cachedColorMap.get(key);
		if (result == null || rgb.equals(result.getRGB()) == false) {
			if (result != null) {
				result.dispose();
			}
			result = new Color(PlatformUI.getWorkbench().getDisplay(), rgb);
			cachedColorMap.put(key, result);
		}

		return result;
	}

	/**
	 * �L�[����RGB���擾����
	 * 
	 * @param key
	 * @return RGB
	 */
	public RGB getRGB(String key) {
		RGB result = PreferenceConverter.getColor(RtcLinkPlugin.getDefault()
				.getPreferenceStore(), key);
		if (result == PreferenceConverter.COLOR_DEFAULT_DEFAULT) { // caution
			// "=="
			result = defaultRGBMap.get(key);
		}

		return result;
	}

	/**
	 * �L�[�ɁARGB���֘A�t����
	 * 
	 * @param key
	 *            �L�[
	 * @param newRGB
	 *            �֘A�t����RGB
	 */
	public void setRGB(String key, RGB newRGB) {
		RGB oldRgb = getRGB(key);

		PreferenceConverter.setValue(RtcLinkPlugin.getDefault()
				.getPreferenceStore(), key, newRGB);

		propertyChangeSupport.firePropertyChange(key, oldRgb, newRGB);
	}

	/**
	 * �Ԋu���擾����
	 * 
	 * @param key
	 *            �L�[
	 * @return �Ԋu
	 */
	public int getInterval(String key) {
		RtcLinkPlugin.getDefault().getPreferenceStore().setDefault(key, -1);

		int result = RtcLinkPlugin.getDefault().getPreferenceStore()
				.getInt(key);
		if (result == -1) { // defaultvalue
			result = defaultInvervalMap.get(key);
		}

		return result;
	}

	/**
	 * �Ԋu��ݒ肷��
	 * 
	 * @param key
	 *            �L�[
	 * @param interval
	 *            �Ԋu
	 */
	public void setInterval(String key, int interval) {
		int oldInterval = getInterval(key);

		RtcLinkPlugin.getDefault().getPreferenceStore().setValue(key, interval);

		propertyChangeSupport.firePropertyChange(key, oldInterval, interval);
	}

	/**
	 * �f�t�H���g�|�[�g���擾����
	 * 
	 * @param key
	 *            �L�[
	 * @return �f�t�H���g�|�[�g
	 */
	public String getDefaultPort(String key) {
		RtcLinkPlugin.getDefault().getPreferenceStore().setDefault(key, "");

		String result = RtcLinkPlugin.getDefault().getPreferenceStore()
				.getString(key);
		if (result.equals("")) { // defaultvalue
			result = defaultConnectionPort;
		}

		return result;
	}

	/**
	 * �f�t�H���g�ڑ��|�[�g��ݒ肷��
	 * 
	 * @param key
	 *            �L�[
	 * @param interval
	 *            �Ԋu
	 */
	public void setDefaultPort(String key, String defaultPort) {
		String oldDefaultPort = getDefaultPort(key);

		RtcLinkPlugin.getDefault().getPreferenceStore().setValue(key, defaultPort);

		propertyChangeSupport.firePropertyChange(key, oldDefaultPort, defaultPort);
	}

	/**
	 * �f�t�H���g�^�C���A�E�g���莞�Ԃ��擾����
	 * 
	 * @param key
	 *            �L�[
	 * @return �f�t�H���g�|�[�g
	 */
	public int getDefaultTimeout(String key) {
		RtcLinkPlugin.getDefault().getPreferenceStore().setDefault(key, -1);

		int result = RtcLinkPlugin.getDefault().getPreferenceStore()
				.getInt(key);
		if (result == -1) { // defaultvalue
			result = defaultTimeoutPeriod;
		}

		return result;
	}

	/**
	 * �f�t�H���g�^�C���A�E�g���莞�Ԃ�ݒ肷��
	 * 
	 * @param key
	 *            �L�[
	 * @param interval
	 *            �Ԋu
	 */
	public void setDefaultTimeout(String key, int defaultTimeout) {
		int oldDefaultTimeout = getDefaultTimeout(key);

		RtcLinkPlugin.getDefault().getPreferenceStore().setValue(key, defaultTimeout);

		propertyChangeSupport.firePropertyChange(key, oldDefaultTimeout, defaultTimeout);
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * �f�t�H���g�F�̃}�b�v���擾����
	 * 
	 * @return �f�t�H���g�F�̃}�b�v
	 */
	public Map<String, RGB> getDefaultRGBMap() {
		return defaultRGBMap;
	}

	/**
	 * �f�t�H���g�Ԋu�̃}�b�v���擾����
	 * 
	 * @return �f�t�H���g�Ԋu�̃}�b�v
	 */
	public Map<String, Integer> getDefaultIntervalMap() {
		return defaultInvervalMap;
	}

}
