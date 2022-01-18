package com.aitd.module_chat.pojo

class AccountConfig {
    class Data{
        var users = mutableListOf<Account>()
    }
    companion object{
        var accList = arrayListOf<Account>()

        fun getAccounts():ArrayList<Account>{
            return accList
        }

        fun getAccountName(id:String):String{
            if (id == "-1"){
                return "所有人"
            }
            for (acc in accList){
                if (acc.userId == id){
                    return acc.name
                }
            }
            return "找不到昵称"
        }
        fun getAccountIcon(id:String):String{
            for (acc in accList){
                if (acc.userId == id){
                    return acc.icon
                }
            }
            return ""
        }
        @JvmStatic
        fun getAccount(id:String): Account?{
            for (acc in accList){
                if (acc.userId == id){
                    return acc
                }
            }
            return null
        }
    }

}