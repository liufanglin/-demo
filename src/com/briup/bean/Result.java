package com.briup.bean;

import android.app.backup.RestoreObserver;


//{"result":"文件保存成功","res":"success"}
public class Result {
	private String result;
	private String res;
	public Result(){}
	public Result(String result,String res){
		this.res=res;
		this.result=result;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	@Override
	public String toString() {
		return "{result:"+result+",res:"+res+"}";
	}
	
	
}
