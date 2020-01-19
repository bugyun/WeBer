package vip.ruoyun.webkit;

import android.content.Context;
import android.support.annotation.NonNull;

public class WeBer {

    static String authority = "provider";

    public static Builder with() {
        return new Builder();
    }

    public interface Interceptor {

        void beforeInit(Context context);
    }

    public static class Builder {

        private boolean isMultiProcessOptimize = false;

        private Interceptor interceptor;

        private Builder() {
        }

        /**
         * 在初始化之前做一些配置
         */
        public Builder interceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public Builder authority(@NonNull String authority) {
            WeBer.authority = authority;
            return this;
        }

        public void build(Context context) {
            if (interceptor != null) {
                interceptor.beforeInit(context);
            }
        }
    }
}
