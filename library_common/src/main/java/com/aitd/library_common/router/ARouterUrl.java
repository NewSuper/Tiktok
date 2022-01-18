package com.aitd.library_common.router;

/**
 * Author : palmer
 * Date   : 2021/6/8
 * E-Mail : lxlfpeng@163.com
 * Desc   : Activity路由，常量命名规范：ROUTE_+模块名+Activity名
 */
public class ARouterUrl {
    public static class Main {
        public static final String ROUTE_MAIN_ACTIVITY = "/module_main/MainActivity";
        public static final String ROUTE_GUIDE_ACTIVITY = "/module_main/GuideActivity";
    }

    public static class Login {
        public static final String ROUTE_LOGIN_ACTIVITY = "/module_login/LoginHomeActivity";
        public static final String ROUTE_REGISTER_ACTIVITY = "/module_login/RegisterActivity";
        public static final String ROUTE_FINFPWD_ACTIVITY = "/module_login/FindPwdActivity";
        public static final String ROUTER_LANGUANG_ACTIVITY = "/module_login/LanguangActivity";
        public static final String ROUTER_SETTING_USER_ACTIVITY = "/module_login/AddUserInfoActivity";
        public static final String ROUTER_BIND_MAIL_ACTIVITY = "/module_login/BingMailActivity";
        public static final String ROUTER_SELECT_COUNTRY_ACTIVITY = "/module_login/SelectCountryActivity";
    }

    public static class Mine {
        public static final String ROUTE_MINE_ACTIVITY = "/module_mine/MineHomeActivity";
        public static final String ROUTE_MINE_FRAGMENT = "/module_mine/MineHomeFragment";
    }

    public static class Chat {
        public static final String ROUTE_CHAT_ACTIVITY = "/module_chat/ui/ChatHomeActivity";
        public static final String ROUTE_CHAT_FRAGMENT = "/module_chat/ui/ChatHomeFragment";

    }

    public static class Wealth {
        public static final String ROUTE_WEALTH_ACTIVITY = "/module_wealth/WealthHomeActivity";
        public static final String ROUTE_WEALTH_FRAGMENT = "/module_wealth/WealthHomeFragment";
    }

    public static class Discover {
        public static final String ROUTE_DISCOVER_ACTIVITY = "/module_discover/DiscoverHomeActivity";
        public static final String ROUTE_DISCOVER_FRAGMENT = "/module_discover/DiscoverHomeFragment";
    }

    public static class PDFViewer {
        public static final String ROUTE_PDF_ACTIVITY = "/pdfviewer/PdfViewerActivity";
    }

    public static class QRCode {
        public static final String ROUTE_SCAN_ACTIVITY = "/qrcode/ScanActivity";
    }


}
