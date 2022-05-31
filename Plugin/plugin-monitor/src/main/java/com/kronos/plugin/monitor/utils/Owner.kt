package com.kronos.plugin.monitor.utils

import com.kronos.plugin.monitor.utils.CmdUtil.executeForOutput
import java.lang.management.ManagementFactory
import java.util.ArrayList


class OwnerProvider : Provider {
    var list = ArrayList<Provider>()
    override fun get(): String {
        for (provider in list) {
            val name = provider.get()
            if (name != null) {
                return name
            }
        }
        return "anonymous"
    }

    init {
        list.add(object : Provider {
            override fun get(): String? {
                return System.getenv()["build_owner"]
            }
        })
        list.add(object : Provider {
            override fun get(): String? {
                val st = HostnameProvider().get()
                return if (st != null && st.startsWith("runner")) {
                    st
                } else null
            }
        })
        list.add(object : Provider {
            override fun get(): String? {
                val st = GitNameProvider().get()
                return if ("fawkes" != st) {
                    st
                } else null
            }
        })
        list.add(GitNameProvider())
        list.add(FilterUserNameProvider(EnvUserNameProvider()))
        list.add(FilterUserNameProvider(JavaEnvUserNameProvider()))
    }
}

class GitEmailProvider : Provider {
    override fun get(): String? {
        try {
            val st =
                executeForOutput("git config user.email", FileUtils.rootFile).trim { it <= ' ' }
            if (st.isNotEmpty()) {
                return st
            }
        } catch (t: Throwable) {
        }
        return null
    }
}


class GitNameProvider : Provider {
    override fun get(): String? {
        try {
            val st =
                executeForOutput("git config user.name", FileUtils.rootFile).trim { it <= ' ' }
            if (st.isNotEmpty()) {
                return st
            }
        } catch (t: Throwable) {
        }
        return null
    }
}


class FilterUserNameProvider constructor(var provider: Provider) : Provider {
    override fun get(): String? {
        val pc = provider.get()
        return if ("root" == pc) {
            null
        } else pc
    }
}

class EnvUserNameProvider : Provider {
    override fun get(): String? {
        val pc = System.getenv("USERNAME")
        return if (pc == null || pc.length == 0) {
            null
        } else pc
    }
}

class PidProvider : Provider {
    override fun get(): String? {
        val runtime = ManagementFactory.getRuntimeMXBean()
        val name = runtime.name // format: "pid@hostname"
        try {
            return name.substring(0, name.indexOf('@'))
        } catch (e: Throwable) {
        }
        return null
    }
}

class JavaEnvUserNameProvider : Provider {
    override fun get(): String? {
        val pc = System.getProperties()["user.name"] as String?
        return if (pc == null || pc.isEmpty()) {
            null
        } else pc
    }
}


class HostnameProvider : Provider {
    override fun get(): String? {
        try {
            val st = executeForOutput("hostname", FileUtils.rootFile).trim { it <= ' ' }
            if (st.isNotEmpty()) {
                return st
            }
        } catch (t: Throwable) {
        }
        return null
    }
}

interface Provider {
    fun get(): String?
}