package jp.go.aist.rtm.RTC;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;

import jp.go.aist.rtm.RTC.log.Logbuf;
import jp.go.aist.rtm.RTC.port.PortBase;
import jp.go.aist.rtm.RTC.util.CallbackFunction;
import jp.go.aist.rtm.RTC.util.StringUtil;

import RTC.RTObject;
  /**
   * {@.ja NamingServer 管理クラス。}
   * {@.en NamingServer management class}
   *
   * <p>
   * {@.ja コンポーネントのNamingServiceへの登録、解除などを管理する。}
   * {@.en Manage to register and unregister components to NamingService.}
   */
public class NamingManager implements CallbackFunction {

    /**
     * {@.ja コンストラクタ。}
     * {@.en Constructor}
     * 
     * @param manager 
     *   {@.ja Managerオブジェクト}
     *   {@.en Manager object}
     */
    public NamingManager(Manager manager) {
        m_manager = manager;

        rtcout = new Logbuf("manager.NamingManager");
    }

    /**
     * {@.ja NameServer の登録。}
     * {@.en Regster the NameServer}
     *
     * <p>
     * {@.ja 指定した形式の NameServer を登録する。
     * 現在指定可能な形式は CORBA のみ。}
     * {@.en Register NameServer by specified format.
     * Currently. only CORBA can be specified.}
     *
     * @param method 
     *   {@.ja NamingService の形式。}
     *   {@.en Format of NamingService}
     * @param name_server 
     *   {@.ja 登録する NameServer の名称}
     *   {@.en Name of NameServer for registration}
     *
     */
    public void registerNameServer(final String method, 
                                                final String name_server) {
        rtcout.println(Logbuf.TRACE, 
            "NamingManager.registerNameServer(" 
            + method + ", " + name_server + ")");
        NamingBase name = createNamingObj(method, name_server);


        synchronized (m_names) {
            m_names.add(new NamingService(method, name_server, name));
        }
    }

    /**
     * {@.ja 指定したオブジェクトのNamingServiceへバインド。}
     * {@.en Bind the specified objects to NamingService}
     * 
     * <p>
     * {@.ja 指定したオブジェクトを指定した名称で CORBA NamingService へバイ
     * ンドする。}
     * {@.en Bind the specified objects to CORBA NamingService 
     * by specified names.}
     * 
     * @param name 
     *   {@.ja バインド時の名称}
     *   {@.en Names at the binding}
     * @param rtobj 
     *   {@.ja バインド対象オブジェクト}
     *   {@.en The target objects for the binding}
     *
     * 
     */
    public void bindObject(final String name, final RTObject_impl rtobj) {
        rtcout.println(Logbuf.TRACE, "NamingManager.bindObject(" + name + ")");
        synchronized (m_names) {
            int len = m_names.size();
            for(int intIdx=0; intIdx < len; ++intIdx ) {
                if( m_names.elementAt(intIdx).ns != null ) {
                    try{
                        m_names.elementAt(intIdx).ns.bindObject(name, rtobj);
                    }
                    catch(Exception ex){
                        m_names.elementAt(intIdx).ns = null;
                    }
                }
            }
            this.registerCompName(name, rtobj);
        }
    }

    public void bindObject(final String name, final PortBase port) {
        rtcout.println(Logbuf.TRACE, "NamingManager.bindObject(" + name + ")");
        synchronized (m_names) {
            int len = m_names.size();
            for(int intIdx=0; intIdx < len; ++intIdx ) {
                if( m_names.elementAt(intIdx).ns != null ) {
                    try{
                        m_names.elementAt(intIdx).ns.bindObject(name, port);
                    }
                    catch(Exception ex){
                        m_names.elementAt(intIdx).ns = null;
                    }
                }
            }
            this.registerPortName(name, port);
        }
    }
    /**
     * {@.ja 指定したManagerServantのNamingServiceへバインド。}
     * {@.en Bind the specified ManagerServants to NamingService}
     *
     * <p> 
     * {@.ja 指定したManagerServantを指定した名称で CORBA NamingService へバ
     * インドする。}
     * {@.en Bind the specified ManagerServants to CORBA NamingService 
     * by specified names.}
     * 
     * @param name 
     *   {@.ja バインド時の名称}
     *   {@.en Names at the binding}
     * @param mgr 
     *   {@.ja バインド対象ManagerServant}
     *   {@.en The target ManagerServants for the binding}
     *
     *
     * 
     */
    public void bindObject(final String name, final ManagerServant mgr) {
        rtcout.println(Logbuf.TRACE, "NamingManager.bindObject(" + name + ")");
        synchronized (m_names) {
            int len = m_names.size();
            for(int intIdx=0; intIdx < len; ++intIdx ) {
                if( m_names.elementAt(intIdx).ns != null ) {
                   try{
                        m_names.elementAt(intIdx).ns.bindObject(name, mgr);
                    }
                    catch(Exception ex){
                        m_names.elementAt(intIdx).ns = null;
                    }
                }
            }
            this.registerMgrName(name, mgr);
        }
    }
    /**
     *
     * {@.ja @brief 指定したポートのNamingServiceへバインド}
     * {@.en Bind the specified port to NamingService}
     *
     * @param name バインド時の名称
     *   {@.ja RTコンポーネント}
     *   {@.ja バインド時の名称}
     *
     * @param port バインド対象のポート
     *   {@.ja バインド対象のポート}
     *   {@.en The target port for the binding}
     *
     * void bindPortObject(const char* name, PortBase* port)
     */
    public void  bindPortObject(String name,PortBase port){
        rtcout.println(Logbuf.TRACE, 
                "NamingManager.bindPortObject(" + name + ")");
        synchronized (m_names) {
            int len = m_names.size();
            for(int intIdx=0; intIdx < len; ++intIdx ) {
                if( m_names.elementAt(intIdx).ns != null ) {
                    try{
                        m_names.elementAt(intIdx).ns.bindPortObject(
                                name, port);
                    }
                    catch(Exception ex){
                        m_names.elementAt(intIdx).ns = null;
                    }
                }
            }
            this.registerPortName(name, port);
        }
    }
    /**
     * {@.ja NamingServer の情報の更新。}
     * {@.en Update information of NamingServer}
     * 
     * <p>
     * {@.ja 設定されている NameServer 内に登録されているオブジェクトの情報を
     * 更新する。}
     * {@.en Update the object information registered 
     * in the specified NameServer.}
     * 
     *
     */
    public void update(){
        rtcout.println(Logbuf.TRACE, "NamingManager.update()");
        
        boolean rebind = StringUtil.toBool(
                    m_manager.getConfig().getProperty("naming.update.rebind"), 
                    "YES", "NO", false);

        synchronized (m_names) {
            int len = m_names.size();
            for( int intIdx=0; intIdx < len; ++intIdx ) {
                if( m_names.elementAt(intIdx).ns == null ) {
                    rtcout.println(Logbuf.DEBUG, "Retrying connection to " 
                                 + m_names.elementAt(intIdx).method + "/" +
                                 m_names.elementAt(intIdx).nsname);
                    retryConnection(m_names.elementAt(intIdx));

                }
                else {
                    try{
                        if( rebind ) {
                            bindCompsTo(m_names.elementAt(intIdx).ns);
                        }
                        if (!m_names.elementAt(intIdx).ns.isAlive()) {
                            rtcout.println(Logbuf.INFO, 
                                 "Name server: " 
                                 + m_names.elementAt(intIdx).nsname + " (" 
                                 + m_names.elementAt(intIdx).method 
                                 + ") disappeared.");

                            m_names.elementAt(intIdx).ns = null;
                        }
                    }
                    catch(Exception ex){
                        rtcout.println(Logbuf.INFO, 
                                 "Name server: " 
                                 + m_names.elementAt(intIdx).nsname + " (" 
                                 + m_names.elementAt(intIdx).method 
                                 + ") disappeared.");

                        m_names.elementAt(intIdx).ns = null;
                    }
                }
            }
        }
    }

    /**
     * {@.ja コンポネントをリバインドする。}
     * {@.en Rebind the component to NameServer}
     * 
     * <p>
     * {@.ja ネームサーバと接続してコンポネントをリバインドする。}
     * {@.en Connect with the NameServer and rebind the component.}
     *
     * @param ns 
     *   {@.ja NameServer}
     *   {@.en  NameServer}
     * 
     */
    protected void retryConnection(NamingService ns){
        // recreate NamingObj
        NamingBase nsobj = null;
        try {
            nsobj = createNamingObj(ns.method, ns.nsname);
            if (nsobj != null) {// if succeed
                rtcout.println(Logbuf.INFO, 
                                 "Connected to a name server: " 
                                 + ns.nsname + "/" 
                                 + ns.method );
                ns.ns = nsobj;
                bindCompsTo(nsobj); // rebind all comps to new NS
                return;
            }
            else {
                rtcout.println(Logbuf.DEBUG, 
                                 "Name service: " + ns.method + "/" 
                                 + ns.nsname  +" still not available.");
            }
        }
        catch (Exception ex) {
            rtcout.println(Logbuf.DEBUG, 
                                 "Name service: " + ns.method + "/" 
                                 + ns.nsname  +" disappeared again.");
            if (nsobj != null) {
                ns.ns = null;
            } 
        }
    }

    /**
     * {@.ja 指定したオブジェクトをNamingServiceからアンバインド。}
     * {@.en Unbind the specified objects from NamingService}
     * 
     * <p>
     * {@.ja 指定したオブジェクトを NamingService からアンバインドする。}
     * {@.en Unbind the specified objects from NamingService.}
     * 
     * @param name 
     *   {@.ja アンバインド対象オブジェクト}
     *   {@.en The target objects for the unbinding}
     */
    public void unbindObject(final String name) {
        rtcout.println(Logbuf.TRACE, "NamingManager.unbindObject(" + name + ")");
        //
        synchronized (m_names) {
            int len = m_names.size();
            for( int intIdx=0; intIdx < len; ++intIdx ) {
                if( m_names.elementAt(intIdx).ns != null ) {
                    m_names.elementAt(intIdx).ns.unbindObject(name);
                }
            }
            unregisterCompName(name);
            unregisterMgrName(name);
            unregisterPortName(name);
        }
    }

    /**
     * {@.ja 全てのオブジェクトをNamingServiceからアンバインド。}
     * {@.en Unbind all objects from NamingService}
     *  
     * <p>
     * {@.ja 全てのオブジェクトを CORBA NamingService からアンバインドする。}
     * {@.en Unbind all objects from CORBA NamingService.}
     * 
     */
    protected void unbindAll() {
        rtcout.println(Logbuf.TRACE, 
            "NamingManager.unbindAll(): m_compNames=" 
            + Integer.toString(m_compNames.size()) 
            + " m_mgrNames=" + Integer.toString(m_mgrNames.size()));

        synchronized (m_compNames) {
            Vector<String> names = new Vector<String>();
            for (int i=0, len=m_compNames.size(); i < len; ++i) {
                names.add(m_compNames.elementAt(i).name);
            }
            for (int i=0; i < names.size(); ++i) {
                unbindObject(names.elementAt(i));
            }
        }
        synchronized (m_mgrNames) {
            Vector<String> names = new Vector<String>();
            // unbindObject modifiy m_mgrNames
            for (int i=0, len=m_mgrNames.size(); i < len; ++i) {
                names.add(m_mgrNames.elementAt(i).name);
            }
            for (int i=0; i < names.size(); ++i) {
                unbindObject(names.elementAt(i));
            }
        }
        synchronized (m_portNames) {
            Vector<String> names = new Vector<String>();
            // unbindObject modifiy m_portNames
            for (int i=0, len=m_portNames.size(); i < len; ++i) {
                names.add(m_portNames.elementAt(i).name);
            }
            for (int i=0; i < names.size(); ++i) {
                unbindObject(names.elementAt(i));
            }
        }
    }

    /**
     *
     * {@.ja バインドされている全てのオブジェクトを取得。}
     * {@.en Get all bound objects}
     * 
     * <p>
     * {@.ja バインドされている全てのオブジェクトを 取得する。}
     *
     * @return 
     *   {@.ja バインド済みオブジェクト リスト}
     *   {@.en Bound object list}
     * 
     */
    protected synchronized Vector<RTObject_impl> getObjects() {
        Vector<RTObject_impl> comps = new Vector<RTObject_impl>();
        int len = m_compNames.size();
        for(int intIdx=0; intIdx < len; ++intIdx) {
            comps.add(m_compNames.elementAt(intIdx).rtobj);
        }
        return comps;
    }

    /**
     *
     * {@.ja 登録したネームサービスのリストを取得する}
     * {@.en Get the list of registered NameServices.}
     *
     * @return 
     *   {@.ja ネームサービスのリスト}
     *   {@.en List of NameService}
     */
    public Vector<NamingService> getNameServices() {
        synchronized (m_names) {
            return (Vector<NamingService>)m_names.clone();
        }
    }
    /**
     *
     * {@.ja 登録したネームサービスのCorbaNamingのリストを取得する}
     * {@.en Get the list of registered CorbaNaming.}
     *
     * @return 
     *   {@.ja CorbaNamingのリスト}
     *   {@.en List of CorbaNaming}
     */
    public ArrayList<CorbaNaming> getCorbaNamings() {
        ArrayList<CorbaNaming> ret = new ArrayList<CorbaNaming>();
        synchronized (m_names) {
            Iterator<NamingService> it = m_names.iterator();
            while (it.hasNext()) {
                NamingBase noc = it.next().ns;
                if(noc == null){
                    continue;
                }
                CorbaNaming cns = noc.getCorbaNaming();
                if(cns == null){
                    continue;
                }
                ret.add(cns);
            }
        }
        return ret;
    }
    /**
     *
     * {@.ja rtcloc形式でRTCのオブジェクトリファレンスを取得}
     * {@.en Gets RTC objects by rtcloc form.}
     *
     * @param name 
     *   {@.ja rtcloc形式でのRTC名
     *   rtcloc://localhost:2809/example/ConsoleIn}
     *   {@.en The RTC name expressed by rtcloc form
     *   rtcloc://localhost:2809/example/ConsoleIn}
     *
     * @return
     *   {@.ja RTCのオブジェクトリファレンス}
     *   {@.en List of RTC objects}
     *
     *  # RTCList string_to_component(string name);
     */
    public RTObject[]  string_to_component(String name){
        rtcout.println(Logbuf.PARANOID, "name: "+name);
        synchronized (m_names) {
            rtcout.println(Logbuf.PARANOID, "m_names.size():"+m_names.size());
            for(int ic=0;ic<m_names.size();++ic){
                RTObject[] comps = m_names.get(ic).ns.string_to_component(name);
                if(comps != null){
                    return comps;
                }
            }
        }
        return null;
    }
    /**
     * {@.ja NameServer 管理用オブジェクトの生成。}
     * {@.en Create objects for NameServer management}
     * 
     * <p>
     * {@.ja 指定した型のNameServer 管理用オブジェクトを生成する。}
     * {@.en Create objects of specified type for NameServer management.}
     *
     * @param method 
     *   {@.ja NamingService 形式}
     *   {@.en NamingService format}
     * @param name_server 
     *   {@.ja NameServer 名称}
     *   {@.en NameServer name}
     * 
     * @return 
     *   {@.ja 生成した NameServer オブジェクト}
     *   {@.en Created NameServer objects}
     * 
     *
     */
    protected NamingBase createNamingObj(final String method, 
                                            final String name_server) {
        rtcout.println(Logbuf.TRACE, 
                    "createNamingObj(method = " 
                    + method + ", nameserver = " + name_server +")");
        String m = method;
        if( m.endsWith("corba")) {
            try {
                NamingOnCorba nameb 
                    = new NamingOnCorba(m_manager.getORB(), name_server);
                NamingBase name = nameb;
                rtcout.println(Logbuf.INFO, 
                    "NameServer connection succeeded: " 
                    + method + "/" + name_server);
                return name;
            } catch (Exception ex) {
                rtcout.println(Logbuf.INFO, 
                    "NameServer connection failed: " 
                    + method +"/" + name_server);
                return null;
            }
        }
        else if( m.endsWith("manager")) {
            NamingBase name = new NamingOnManager(m_manager.getORB(),m_manager);
            return name;
        }
        return null;
    }

    /**
     * {@.ja 設定済みコンポーネントを NameServer に登録。}
     * {@.en Register the configured component to NameServer}
     * 
     * <p>
     * {@.ja 設定済みコンポーネントを指定した NameServer に登録する。}
     * {@.en Register the already configured components to NameServer.}
     *
     * @param ns 
     *   {@.ja 登録対象 NameServer}
     *   {@.en The target NameServer for the registration}
     */
    protected void bindCompsTo(NamingBase ns) {
        int len = m_compNames.size();
        for( int intIdx=0; intIdx < len; ++intIdx) {
            ns.bindObject(m_compNames.elementAt(intIdx).name, m_compNames.elementAt(intIdx).rtobj);
        }
    }

    /**
     * {@.ja NameServer に登録するコンポーネントの設定。}
     * {@.en Configure the components that will be registered to NameServer}
     * 
     * <p>
     * {@.ja NameServer に登録するコンポーネントを設定する。
     * 対象コンポーネントが既に登録済みの場合は何もしない。}
     * {@.en Configure the components that will be registered to NameServer.}
     *
     * @param name 
     *   {@.ja コンポーネントの登録時名称}
     *   {@.en Names of components at the registration}
     * @param rtobj 
     *   {@.ja 登録対象オブジェクト}
     *   {@.en The target objects for registration}
     */
    protected void registerCompName(final String name, final RTObject_impl rtobj) {
        int len = m_compNames.size();
        for(int intIdx=0; intIdx < len; ++intIdx ) {
            if( m_compNames.elementAt(intIdx).name.equals(name) ) {
                m_compNames.elementAt(intIdx).rtobj = rtobj;
                return;
            }
        }
        m_compNames.add(new Comps(name, rtobj));
        return;
    }

    /**
     *
     * {@.ja NameServer に登録するPortの設定}
     * {o.en Configure the ports that will be registered to NameServer}
     * 
     * <p>
     * {@.ja NameServer に登録するPortを設定する。}
     * {@.en Configure the ports that will be registered to NameServer.}
     *
     * @param name
     *   {@.ja コンポーネントの登録時名称}
     *   {@.en Names of components at the registration}
     * 
     * @param port
     *   {@.ja 登録対象port}
     *   {@.en The target ports for registration}
     *
     */
    protected void registerPortName(final String name, final PortBase port) {
        int len = m_portNames.size();
        for(int intIdx=0; intIdx < len; ++intIdx ) {
            if( m_portNames.elementAt(intIdx).name.equals(name) ) {
                m_portNames.elementAt(intIdx).port = port;
                return;
            }
        }
        m_portNames.add(new Port(name, port));
        return;
    }

    /**
     * {@.ja NameServer に登録するManagerServantの設定。}
     * {@.en Configure the ManagerServants that will be registered 
     * to NameServer}
     * 
     * <p>
     * {@.ja NameServer に登録するManagerServantを設定する。
     * 対象マネージャサーバントが既に登録済みの場合は何もしない。}
     * {@.en Configure the ManagerServants that will be registered 
     * to NameServer.}
     *
     * @param name 
     *   {@.ja ManagerServantの登録時名称}
     *   {@.en Names of ManagerServants at the registration}
     * @param mgr 
     *   {@.ja 登録対象ManagerServant}
     *   {@.en The target ManagerServants for registration}
     */
    protected void registerMgrName(final String name, final ManagerServant mgr) {
        int len = m_mgrNames.size();
        for(int intIdx=0; intIdx < len; ++intIdx ) {
            if( m_mgrNames.elementAt(intIdx).name.equals(name) ) {
                m_mgrNames.elementAt(intIdx).mgr = mgr;
                return;
            }
        }
        m_mgrNames.add(new Mgr(name, mgr));
        return;
    }

    /**
     * {@.ja NameServer に登録するコンポーネントの設定解除。}
     * {@.en Unregister the components that will be registered to NameServer}
     * 
     * <p>
     * {@.ja NameServer に登録するコンポーネントの設定を解除する。}
     *
     * @param name 
     *   {@.ja 設定解除対象コンポーネントの名称}
     *   {@.en Names of the target components for unregistration}
     */
    protected void unregisterCompName(final String name) {
        int len = m_compNames.size();
        for( int intIdx=0; intIdx < len; ++intIdx ) {
            if( m_compNames.elementAt(intIdx).name.equals(name)) {
                m_compNames.remove(m_compNames.elementAt(intIdx));
                return;
            }
        }
        return;
    }

    /**
     * {@.ja NameServer に登録するポートの設定解除}
     * {@.en releases setting in the port registered with NameServer.}
     *
     * @param name 
     *   {@.ja 設定解除対象ポートの名称}
     *   {@.en portname}
     *
     */
    protected void unregisterPortName(final String name) {
        int len = m_portNames.size();
        for( int intIdx=0; intIdx < len; ++intIdx ) {
            if( m_portNames.elementAt(intIdx).name.equals(name)) {
                m_portNames.remove(m_portNames.elementAt(intIdx));
                return;
            }
        }
        return;
    }
    /**
     * {@.ja NameServer に登録するManagerServantの設定解除。}
     * {@.en Unregister the ManagerServants that will be registered 
     * to NameServer}
     * 
     * <p>
     * {@.ja NameServer に登録するManagerServantの設定を解除する。}
     *
     * @param name 
     *   {@.ja 設定解除対象ManagerServantの名称}
     *   {@.en Names of the target ManagerServants for unregistration}
     * 
     */
    protected void unregisterMgrName(final String name) {
        int len = m_mgrNames.size();
        for( int intIdx=0; intIdx < len; ++intIdx ) {
            if( m_mgrNames.elementAt(intIdx).name.equals(name)) {
                m_mgrNames.remove(m_mgrNames.elementAt(intIdx));
                return;
            }
        }
        return;
    }

    /**
     * {@.ja NameServer 管理用クラス}
     * {@.en Class for NameServer management}
     */
    protected class Names {
        /**
         * {@.ja コンストラクタ。}
         * {@.en Constructor}
         * 
         * @param meth 
         *   {@.ja NamingServerタイプ}
         *   {@.en NamingServer type}
         * @param name 
         *   {@.ja NamingServer名称}
         *   {@.en NamingServer name}
         * @param naming 
         *   {@.ja NameServerオブジェクト}
         *   {@.en NamingServer object}
         *
         */
        public Names(final String meth, final String name, NamingBase naming) {
            method = meth;
            nsname = name;
            ns = naming;
        }

        /**
         * {@.ja NamingServerタイプ}
         * {@.en NamingServer type}
         */
        public String method;
        /**
         * {@.ja NamingServer名称}
         * {@.en NamingServer name}
         */
        public String nsname;
        /**
         * {@.ja NameServerオブジェクト}
         * {@.en NameServer object}
         */
        public NamingBase ns;
    }
    /**
     * {@.ja 登録された NameServer リスト}
     * {@.en NameServer list}
     */
    protected Vector<NamingService> m_names = new Vector<NamingService>();
    /**
     * {@.ja Naming Service登録用コンポーネントクラス}
     * {@.en Class for component management}
     */
    protected class Comps {
        /**
         * {@.ja コンストラクタ。}
         * {@.en Constructor}
         * 
         * @param n 
         *   {@.ja コンポーネント名称}
         *   {@.en Component name}
         * @param obj 
         *   {@.ja 登録対象オブジェクト}
         *   {@.en object}
         */
        public Comps(final String n, final RTObject_impl obj) {
            name = n;
            rtobj = obj;
        }
        /**
         * {@.ja コンポーネント名称}
         * {@.en Component name}
         */
        public String name;
        /**
         * {@.ja 登録対象オブジェクト}
         * {@.en object}
         */
        public RTObject_impl rtobj;
    }

    /**
     * {@.ja コンポーネント管理用構造体}
     * {@.en Structure for component management}
     */
    protected class Port {
      public Port(final String n, final PortBase p) {
          name = n;
          port = p;
      }
      public String name;
      public PortBase port;
    };
    
    /**
     * {@.ja Naming Service登録用マネージャサーバントクラス}
     * {@.en Class for ManagerServant management}
     */
    protected class Mgr {
        /**
         * {@.ja コンストラクタ。}
         * {@.en Constructor}
         * 
         * @param n
         *   {@.ja 名称}
         *   {@.en name}
         * @param obj
         *   {@.ja オブジェクト}
         *   {@.en object}
         */
        public Mgr(final String n, final ManagerServant obj) {
            name = n;
            mgr = obj;
        }
        /**
         * {@.ja コンポーネント名称}
         * {@.en Component name}
         */
        public String name;
        /**
         *  {@.ja 登録対象マネージャサーバント}
         *  {@.en ManagerServant}
         */
        public ManagerServant mgr;
    }
    /**
     * {@.ja 登録されたコンポーネントリスト}
     * {@.en Component list}
     */
    protected Vector<Comps> m_compNames = new Vector<Comps>();

    /**
     * {@.ja コンポーネントリスト}
     * {@.en Component list}
     */
    protected Vector<Port> m_portNames = new Vector<Port>();

    /**
     * {@.ja 登録されたManagerServantリスト}
     * {@.en ManagerServant list}
     */
    protected Vector<Mgr> m_mgrNames = new Vector<Mgr>();

    /**
     * {@.ja タイマーに登録されたリスナーから呼び出されるメソッド}
     * {@.en Method that calls from listener registered in timer}
     */
    public void doOperate() {
        this.update();
    }

    /**
     * {@.ja Managerオブジェクト}
     * {@.en Manager object}
     */
    protected Manager m_manager;
    /**
     * {@.ja Logging用フォーマットオブジェクト}
     * {@.en Format object for Logging}
     */
    protected Logbuf rtcout;
}
