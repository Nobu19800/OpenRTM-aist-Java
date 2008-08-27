package jp.go.aist.rtm.rtctemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.go.aist.rtm.rtctemplate.corba.idl.parser.IDLParser;
import jp.go.aist.rtm.rtctemplate.corba.idl.parser.ParseException;
import jp.go.aist.rtm.rtctemplate.corba.idl.parser.syntaxtree.specification;
import jp.go.aist.rtm.rtctemplate.generator.GeneratedResult;
import jp.go.aist.rtm.rtctemplate.generator.IDLParamConverter;
import jp.go.aist.rtm.rtctemplate.generator.PreProcessor;
import jp.go.aist.rtm.rtctemplate.generator.param.DataPortParam;
import jp.go.aist.rtm.rtctemplate.generator.param.GeneratorParam;
import jp.go.aist.rtm.rtctemplate.generator.param.RtcParam;
import jp.go.aist.rtm.rtctemplate.generator.param.ServiceReferenceParam;
import jp.go.aist.rtm.rtctemplate.generator.param.idl.ServiceClassParam;
import jp.go.aist.rtm.rtctemplate.generator.parser.MergeBlockParser;
import jp.go.aist.rtm.rtctemplate.manager.CXXGenerateManager;
import jp.go.aist.rtm.rtctemplate.manager.CommonGenerateManager;
import jp.go.aist.rtm.rtctemplate.manager.GenerateManager;
import jp.go.aist.rtm.rtctemplate.manager.PythonGenerateManager;

import org.eclipse.jface.dialogs.IDialogConstants;

/**
 * �W�F�l���[�^�N���X
 */
public class Generator {
	
	private HashMap<String, GenerateManager> generateManagerList = new HashMap<String, GenerateManager>();

	public Generator() {
		this.addGenerateManager("Common", new CommonGenerateManager());
		this.addGenerateManager( RtcParam.LANG_PYTHON, new PythonGenerateManager());
		this.addGenerateManager( RtcParam.LANG_CPP, new CXXGenerateManager());
	}
	
	/**
	 * �W�F�l���[�g�E�}�l�[�W����ǉ�����
	 * 
	 * @param genManager�@�����Ώۂ̃W�F�l���[�g�E�}�l�[�W��
	 */
	public void addGenerateManager(String managerKey, GenerateManager genManager) {
		generateManagerList.put(managerKey, genManager);
	}
	/**
	 * �W�F�l���[�g�E�}�l�[�W�����N���A����
	 */
	public void clearGenerateManager() {
		generateManagerList = new HashMap<String, GenerateManager>();
	}
	/**
	 * �W�F�l���[�g����
	 * 
	 * @param generatorParam
	 *            �p�����[�^
	 * @return GeneratedResult�̃��X�g
	 * @throws ParseException
	 *             IDL�̃p�[�X�Ɏ��s�����ꍇ�Ȃ�
	 */
	public List<GeneratedResult> doGenerate(GeneratorParam generatorParam)
			throws Exception {

		validate(generatorParam);

//		List<ServiceClassParam> serviceClassParamList = filterReferencedServiceParam(
//				getRtcServiceClass(generatorParam), generatorParam);
		checkReferencedServiceParam(getRtcServiceClass(generatorParam), generatorParam);
		List<ServiceClassParam> serviceClassParamList = new ArrayList<ServiceClassParam>();
		List<String> serviceClassNameList = new ArrayList<String>();
		for( ServiceClassParam serviceClassParam : getRtcServiceClass(generatorParam) ) {
			if( !serviceClassNameList.contains(serviceClassParam.getName()) ) {
				serviceClassNameList.add(serviceClassParam.getName());
				serviceClassParamList.add(serviceClassParam);
			}
		}

		generatorParam.getServiceClassParams().clear();

		for (ServiceClassParam param : serviceClassParamList) {
			param.setParent(generatorParam);
			generatorParam.getServiceClassParams().add(param);
		}

		List<GeneratedResult> result = new ArrayList<GeneratedResult>();
		for (GenerateManager manager : generateManagerList.values() ) {
			result.addAll(manager.doGenerate(generatorParam));
		}

		return result;
	}

	/**
	 * �o���f�[�g���s��
	 * 
	 * @param generatorParam
	 */
	private void validate(GeneratorParam generatorParam) {
		if (generatorParam.getOutputDirectory() == null) {
			throw new RuntimeException("OutputDirectory�����݂��܂���B");
		}
		
		List<String> providerIdlPathList = new ArrayList<String>();
		List<String> consumerIdlPathList = new ArrayList<String>();
		for(String idlPath :  generatorParam.getProviderIDLPathParams()) {
			if( !providerIdlPathList.contains(idlPath) )
				providerIdlPathList.add(idlPath);
		}
		for(String idlPath :  generatorParam.getConsumerIDLPathParams()) {
			if( !providerIdlPathList.contains(idlPath) && !consumerIdlPathList.contains(idlPath) )
				consumerIdlPathList.add(idlPath);
		}
		
		for (RtcParam rtcParam : generatorParam.getRtcParams()) {
			if (rtcParam.getName() == null) {
				throw new RuntimeException("Module Name�����݂��܂���B");
			}
			if (rtcParam.getMaxInstance() == -1) {
				throw new RuntimeException("Max Instance�����݂��܂���B");
			}

			List portNames = new ArrayList();
			for (DataPortParam port : rtcParam.getInports()) {
				if (portNames.contains(port.getName())) {
					throw new RuntimeException("Port�ɓ������O�����݂��܂��B :"
							+ rtcParam.getName());
				} else {
					portNames.add(port.getName());
				}
			}
			for (DataPortParam port : rtcParam.getOutports()) {
				if (portNames.contains(port.getName())) {
					throw new RuntimeException("Port�ɓ������O�����݂��܂��B :"
							+ rtcParam.getName());
				} else {
					portNames.add(port.getName());
				}
			}

			List referenceNames = new ArrayList();
			for (ServiceReferenceParam serviceReferenceParam : rtcParam
					.getProviderReferences()) {
				if (referenceNames.contains(serviceReferenceParam.getName())) {
					throw new RuntimeException(
							"Provider��������Consumer�ɓ������O�����݂��܂��B :"
									+ rtcParam.getName());
				} else {
					referenceNames.add(serviceReferenceParam.getName());
				}
			}
			for (ServiceReferenceParam serviceReferenceParam : rtcParam
					.getConsumerReferences()) {
				if (referenceNames.contains(serviceReferenceParam.getName())) {
					throw new RuntimeException(
							"Provider��������Consumer�ɓ������O�����݂��܂��B :"
									+ rtcParam.getName());
				} else {
					referenceNames.add(serviceReferenceParam.getName());
				}
			}

			List referenceInterfaceNames = new ArrayList();
			for (ServiceReferenceParam serviceReferenceParam : rtcParam
					.getProviderReferences()) {
				if (referenceInterfaceNames.contains(serviceReferenceParam
						.getInterfaceName())) {
					throw new RuntimeException(
							"Provider��������Consumer�ɓ���PortName�����݂��܂��B :"
									+ rtcParam.getName());
				} else {
					referenceInterfaceNames.add(serviceReferenceParam
							.getInterfaceName());
				}
			}
			for (ServiceReferenceParam serviceReferenceParam : rtcParam
					.getConsumerReferences()) {
				if (referenceInterfaceNames.contains(serviceReferenceParam
						.getInterfaceName())) {
					throw new RuntimeException(
							"Provider��������Consumer�ɓ���PortName�����݂��܂��B :"
									+ rtcParam.getName());
				} else {
					referenceInterfaceNames.add(serviceReferenceParam
							.getInterfaceName());
				}
			}
			
			rtcParam.setProviderIdlPathes(providerIdlPathList);
			rtcParam.setConsumerIdlPathes(consumerIdlPathList);
		}

	}

	/**
	 * �Q�Ƃ���Ă���Service�����݂��邩�m�F����
	 * 
	 * @param rtcServiceClasses
	 * @param generatorParam
	 * @return
	 */
	private void checkReferencedServiceParam(List<ServiceClassParam> rtcServiceClasses,GeneratorParam generatorParam) {

		for (RtcParam param : generatorParam.getRtcParams()) {
			List<ServiceReferenceParam> serviceReferenceParamList = new ArrayList<ServiceReferenceParam>();
			serviceReferenceParamList.addAll(param.getProviderReferences());
			serviceReferenceParamList.addAll(param.getConsumerReferences());

			for (ServiceReferenceParam serviceReferenceParam : serviceReferenceParamList) {
				ServiceClassParam find = null;
				for (ServiceClassParam serviceClassParam : rtcServiceClasses) {
					if (serviceReferenceParam.getType().equals(serviceClassParam.getName())) {
						find = serviceClassParam;
						break;
					}
				}

				if (find == null)
					throw new RuntimeException("'" + serviceReferenceParam.getType() + "' is not found in IDL");
			}
		}
	}

//	/**
//	 * �Q�Ƃ���Ă���Service�݂̂Ƀt�B���^����
//	 * 
//	 * @param rtcServiceClasses
//	 * @param generatorParam
//	 * @return
//	 */
//	private List<ServiceClassParam> filterReferencedServiceParam(
//			List<ServiceClassParam> rtcServiceClasses,
//			GeneratorParam generatorParam) {
//		Set<ServiceClassParam> result = new HashSet<ServiceClassParam>();
//
//		for (RtcParam param : generatorParam.getRtcParams()) {
//			List<ServiceReferenceParam> serviceReferenceParamList = new ArrayList<ServiceReferenceParam>();
//			serviceReferenceParamList.addAll(param.getProviderReferences());
//			serviceReferenceParamList.addAll(param.getConsumerReferences());
//
//			for (ServiceReferenceParam serviceReferenceParam : serviceReferenceParamList) {
//				ServiceClassParam find = null;
//				for (ServiceClassParam serviceClassParam : rtcServiceClasses) {
//					if (serviceReferenceParam.getType().equals(
//							serviceClassParam.getName())) {
//						find = serviceClassParam;
//						break;
//					}
//				}
//
//				if (find != null) {
//					result.add(find);
//				} else {
//					throw new RuntimeException("'"
//							+ serviceReferenceParam.getType()
//							+ "' is not found in IDL");
//				}
//			}
//		}
//
//		return Arrays.asList(result
//				.toArray(new ServiceClassParam[result.size()]));
//	}

	/**
	 * �T�[�r�X�N���X���擾����
	 * 
	 * @param generatorParam
	 * @return
	 * @throws ParseException
	 */
	private List<ServiceClassParam> getRtcServiceClass(
			GeneratorParam generatorParam) throws ParseException {
		List<ServiceClassParam> result = new ArrayList<ServiceClassParam>();

		for(int intIdx=0; intIdx<generatorParam.getProviderIDLPathParams().size(); intIdx++ ) {
			if (generatorParam.getProviderIDLContents(intIdx) != null) {
				String idl = PreProcessor.parse(generatorParam
						.getProviderIDLContents(intIdx), generatorParam
						.getIncludeIDLDic());
				IDLParser parser = new IDLParser(new StringReader(idl));

				specification spec = parser.specification();

				List<ServiceClassParam> serviceClassParams = IDLParamConverter
						.convert(spec, generatorParam.getProviderIDLPath(intIdx));
				result.addAll(serviceClassParams);
			}
		}
		for(int intIdx=0; intIdx<generatorParam.getConsumerIDLPathParams().size(); intIdx++ ) {
			if (generatorParam.getConsumerIDLContents(intIdx) != null) {
				String idl = PreProcessor.parse(generatorParam
						.getConsumerIDLContents(intIdx), generatorParam
						.getIncludeIDLDic());
				IDLParser parser = new IDLParser(new StringReader(idl));
	
				specification spec = parser.specification();
	
				result.addAll(IDLParamConverter.convert(spec, generatorParam
						.getConsumerIDLPath(intIdx)));
			}
		}

//		if (generatorParam.getProviderIDLContents() != null) {
//			String idl = PreProcessor.parse(generatorParam
//					.getProviderIDLContents(), generatorParam
//					.getIncludeIDLDic());
//			IDLParser parser = new IDLParser(new StringReader(idl));
//
//			specification spec = parser.specification();
//
//			List<ServiceClassParam> serviceClassParams = IDLParamConverter
//					.convert(spec, generatorParam.getProviderIDLPath());
//			result.addAll(serviceClassParams);
//		}
//		if (generatorParam.getConsumerIDLContents() != null) {
//			String idl = PreProcessor.parse(generatorParam
//					.getConsumerIDLContents(), generatorParam
//					.getIncludeIDLDic());
//			IDLParser parser = new IDLParser(new StringReader(idl));
//
//			specification spec = parser.specification();
//
//			result.addAll(IDLParamConverter.convert(spec, generatorParam
//					.getConsumerIDLPath()));
//		}

		return result;
	}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	private void writeFile(List<GeneratedResult> generatedResultList,
			String outputDirectory, MergeHandler handler) throws IOException {
		for (GeneratedResult generatedResult : generatedResultList) {
			if (generatedResult.getName().equals("") == false) {
				writeFile(generatedResult, outputDirectory, handler);
			}
		}
	}

	private void writeFile(GeneratedResult generatedResult, String strOutDir,
			MergeHandler handler) throws IOException {
		
		File outputDirectory = new File(strOutDir);
		if (outputDirectory.exists() == false) {
			throw new RuntimeException("�o�͑Ώۂ̃f�B���N�g����������܂���");
		}
		
		File targetFile = new File(outputDirectory, generatedResult.getName());

		boolean isOutput = false;
		if (targetFile.exists()) {
			String originalFileContents = Util.readFile(targetFile.getAbsolutePath());
			if (originalFileContents.equals(generatedResult.getCode()) == false) {
				int selectedProcess = handler.getSelectedProcess(generatedResult, originalFileContents);
				if (selectedProcess != MergeHandler.PROCESS_ORIGINAL_ID
						&& selectedProcess != IDialogConstants.CANCEL_ID) {
					targetFile.renameTo(new File((strOutDir + "\\" + generatedResult.getName())+ DATE_FORMAT.format(new GregorianCalendar().getTime())));

					if (selectedProcess == MergeHandler.PROCESS_MERGE_ID) {
						generatedResult.setCode(MergeBlockParser.merge(
								originalFileContents, MergeBlockParser
										.parse(generatedResult.getCode())));
					}

					isOutput = true;
				}
			}
		} else {
			isOutput = true;
		}

		if (targetFile.exists() == false) {
			targetFile.createNewFile();
		}

		if (isOutput) {
			FileOutputStream fos = new FileOutputStream(new File(strOutDir,
					generatedResult.getName()));
			OutputStreamWriter osw = new OutputStreamWriter(fos, System
					.getProperty("file.encoding"));
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(generatedResult.getCode());
			bw.close();
			osw.close();
			fos.close();
		}
	}

	/**
	 * �W�F�l���[�g���A�t�@�C���o�͂��s��
	 * 
	 * @param generatorParam
	 *            �p�����[�^
	 * @param handler
	 *            MergeHandler
	 * @throws ParseException
	 * @throws IOException
	 */
	public void doGenerateWrite(GeneratorParam generatorParam,
			MergeHandler handler) throws Exception {
		
		List<GeneratedResult> generatedResult = doGenerate(generatorParam);
		writeFile(generatedResult, generatorParam.getOutputDirectory(), handler);
	}

	/**
	 * �}�[�W�n���h��
	 */
	public interface MergeHandler {
		/**
		 * �v���Z�X�F�I���W�i�����c��
		 */
		public static final int PROCESS_ORIGINAL_ID = 10;

		/**
		 * �v���Z�X�F�V���������������̂𗘗p����
		 */
		public static final int PROCESS_GENERATE_ID = 20;

		/**
		 * �v���Z�X�F�}�[�W���s��
		 */
		public static final int PROCESS_MERGE_ID = 30;

		/**
		 * �v���Z�X��I������
		 * 
		 * @param generatedResult
		 *            ��������
		 * @param originalFileContents
		 *            �����t�@�C���̓��e
		 * @return
		 */
		public int getSelectedProcess(GeneratedResult generatedResult,
				String originalFileContents);
	}
}
