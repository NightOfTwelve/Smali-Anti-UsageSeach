package Native;

import java.util.ArrayList;

import Smali.SmaliMethod;

public class NativeMethod {
	private String returnType;
	private String methodName;
	private String args;
	private StringBuilder code;
	public NativeMethod(String r,String m,String a,StringBuilder c){
		this.returnType = r;
		this.methodName = m;
		this.args = a;
		this.code = c;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	public StringBuilder getCode() {
		return code;
	}
	public void setCode(StringBuilder code) {
		this.code = code;
	}
	public static NativeMethod smaliMethod2NativeMethod(SmaliMethod srcSmaliMethod){
		StringBuilder args = new StringBuilder("JNIEnv *env, jobject obj");
		StringBuilder callBackArgs = new StringBuilder();
		ArrayList<String> smaliArgs = srcSmaliMethod.getArgs();
		for(int i = 0;i < smaliArgs.size() ;i++){
			if(i == 0){
				args.append(",");
				callBackArgs.append(",");
			}
			args.append(NativeHelper.SmaliType2NativeType(smaliArgs.get(i)));
			args.append(" arg" + String.valueOf(i));
			callBackArgs.append("arg"+String.valueOf(i));
			if(i != smaliArgs.size()-1){
				args.append(",");
				callBackArgs.append(",");
			}
		}
		StringBuilder codes = new StringBuilder("    jclass cls = env->GetObjectClass(obj);\r\n");
		String isStatic = "";
		if(srcSmaliMethod.isStatic()){
			isStatic = "Static";
		}
		codes.append("    jmethodID callback = env->Get"+isStatic+"MethodID(cls,\""+srcSmaliMethod.getName()+"\",\"("+srcSmaliMethod.getArgsSig()+")"+srcSmaliMethod.getReturnTypeStr()+"\");\r\n");
		codes.append("    ");
		if(!srcSmaliMethod.getReturnTypeStr().equals("V")){
			codes.append("return ");
			codes.append("("+NativeHelper.SmaliType2NativeType(srcSmaliMethod.getReturnTypeStr())+")env->Call"+isStatic+"ObjectMethod(obj,callback"+callBackArgs+");");
		}
		else{
			codes.append("("+NativeHelper.SmaliType2NativeType(srcSmaliMethod.getReturnTypeStr())+")env->Call"+isStatic+"VoidMethod(obj,callback"+callBackArgs+");");
		}
		NativeMethod nativeMethod = new NativeMethod(NativeHelper.SmaliType2NativeType(srcSmaliMethod.getReturnTypeStr()),srcSmaliMethod.getName()+"_native" ,args.toString(),codes);
		return nativeMethod;
	}
}
