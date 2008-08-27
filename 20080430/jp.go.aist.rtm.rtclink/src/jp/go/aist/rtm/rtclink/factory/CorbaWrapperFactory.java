package jp.go.aist.rtm.rtclink.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jp.go.aist.rtm.rtclink.model.component.Connector;
import jp.go.aist.rtm.rtclink.model.core.WrapperObject;
import jp.go.aist.rtm.rtclink.model.nameservice.NameServerNamingContext;
import jp.go.aist.rtm.rtclink.model.nameservice.NameServiceReference;
import jp.go.aist.rtm.rtclink.model.nameservice.impl.NameServiceReferenceImpl;
import jp.go.aist.rtm.rtclink.synchronizationframework.SynchronizationManager;
import jp.go.aist.rtm.rtclink.synchronizationframework.mapping.MappingRule;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;

/**
 * RtcLink�̓����Ŏg�p�����h���C���I�u�W�F�N�g���쐬����t�@�N�g��
 * <p>
 * �����ł́A�����t���[�����[�N���g�p���Ă���B
 */
public class CorbaWrapperFactory {

    private static CorbaWrapperFactory __instance = null;

    private SynchronizationManager synchronizationManager;

    /**
     * �R���X�g���N�^
     * <p>
     * ���̃}�b�s���O���[�����g�p�����t�@�N�g�����쐬���邱�Ƃ��ł���悤�ɃR���X�g���N�^�����J���邪�A��{�I�ɂ�getInstance()�𗘗p���ăV���O���g�����쐬���邱��
     * 
     * @param mappingRules
     */
    public CorbaWrapperFactory(MappingRule[] mappingRules) {
        synchronizationManager = new SynchronizationManager(mappingRules);
    }

    /**
     * �t�@�N�g���̃V���O���g�����擾����
     * 
     * @return �t�@�N�g���̃V���O���g��
     */
    public static CorbaWrapperFactory getInstance() {
        if (__instance == null) {
            __instance = new CorbaWrapperFactory(MappingRuleFactory
                    .getMappingRule());
        }

        return __instance;
    }

    /**
     * �����[�g�I�u�W�F�N�g��n���A�h���C���I�u�W�F�N�g���쐬����
     * 
     * @param remoteObject
     *            �����[�g�I�u�W�F�N�g
     * @return �쐬���ꂽ�h���C���I�u�W�F�N�g
     */
    public WrapperObject createWrapperObject(Object... remoteObjects) {
        return (WrapperObject) synchronizationManager
                .createLocalObject(remoteObjects);
    }

    /**
     * �l�[���R���e�N�X�g�I�u�W�F�N�g�ƃl�[���T�[�o������A�l�[���T�[�o�̃h���C���I�u�W�F�N�g���쐬����
     * 
     * @param namingContext
     *            �l�[�~���O�R���e�N�X�g
     * @param nameServerName
     *            �l�[���T�[�o��
     * @return �l�[���T�[�o�̃h���C���I�u�W�F�N�g
     */
    public NameServerNamingContext getNameServiceContextCorbaWrapper(
            NamingContextExt namingContext, String nameServerName) {

        NameServiceReference nameServiceReference = new NameServiceReferenceImpl();
        nameServiceReference.setNameServerName(nameServerName);
        Binding binding = new Binding();
        binding.binding_name = new NameComponent[] {};
        nameServiceReference.setBinding(binding);
        nameServiceReference.setRootNamingContext(namingContext);

        NameServerNamingContext result = (NameServerNamingContext) createWrapperObject(
                namingContext, nameServiceReference);

        return result;
    }

    /**
     * XML����h���C���I�u�W�F�N�g�c���[�𕜌�����
     * <p>
     * �����ł́AEMF�̃I�u�W�F�N�g�����[�h���A�����t���[�����[�N�̃I�u�W�F�N�g�̕������s��
     * 
     * @param resource
     *            ���\�[�X
     * @return �h���C���I�u�W�F�N�g���[�g
     * @throws IOException
     *             �t�@�C�����ǂݍ��߂Ȃ��ꍇ�Ȃ�
     */
    public EObject loadContentFromResource(Resource resource)
            throws IOException {
        resource.load(Collections.EMPTY_MAP);
        EObject object = (EObject) resource.getContents().get(0);
        synchronizationManager.assignSynchonizationSupport(object);

        Rehabilitation.rehabilitation(object);

        return object;
    }

    /**
     * XML�ɃI�u�W�F�N�g�c���[��ۑ�����
     * <p>
     * �����ł́AEMF�̃I�u�W�F�N�g���Z�[�u����B �����t���[�����[�N�̃I�u�W�F�N�g�̓Z�[�u����Ȃ��̂ŁA���[�h���ɕ������s���K�v������B
     * 
     * @param resource
     *            ���\�[�X
     * @param content
     *            �h���C���I�u�W�F�N�g���[�g
     * @throws IOException
     *             �t�@�C���ɕۑ��ł��Ȃ��ꍇ�Ȃ�
     */
    public void saveContentsToResource(Resource resource, EObject content)
            throws IOException {
        resource.getContents().add(content);

        resource.save(Collections.EMPTY_MAP);
    }

    /**
     * �Ώۂ̃I�u�W�F�N�g���R�s�[���܂�
     * 
     * @param component
     * @return
     */
    public WrapperObject copy(EObject obj) {
        WrapperObject copy = (WrapperObject) EcoreUtil.copy(obj);

        List<Connector> connectors = new ArrayList<Connector>();
        for (Iterator iter = copy.eAllContents(); iter.hasNext();) {
            Object e = iter.next();
            if (e instanceof Connector) {
                connectors.add((Connector) e);
            }
        }
        for (Connector connector : connectors) {
            EcoreUtil.remove(connector);
        }

        synchronizationManager.assignSynchonizationSupport(copy);

        return copy;
    }

    /**
     * SynchronizationManager���擾����
     */
    public SynchronizationManager getSynchronizationManager() {
        return synchronizationManager;
    }
}
