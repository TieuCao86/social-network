import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Mail, Lock, Eye, EyeOff } from "lucide-react";
import { loginSchema, type LoginFormData } from "@social/shared";
import { useAuth } from "../hooks/useAuth";

import { GradientButton } from "../components/ui/GradientButton";

import logoImg_N from "../assets/logo_N.png";
import logoImg_D from "../assets/logo_D.png";
import communityImg from "../assets/community.png";

export function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);
  const { login, isLoggingIn } = useAuth();

  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      phoneOrEmail: "",
      password: "",
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data);

      alert("Đăng nhập thành công");

      navigate("/home", { replace: true });
    } catch (err: any) {
      alert(err.message || "Đăng nhập thất bại");
    }
  };

  return (
    <div className="min-h-screen w-full flex flex-col lg:flex-row overflow-x-hidden">
      {/* NỬA TRÁI (50%): Nền #e9f5f5 */}
      <div className="hidden lg:flex w-1/2 min-h-screen bg-[#e9f5f5] flex-col justify-between p-12 xl:p-16">
        {/* Logo góc trên bên trái */}
        <div className="w-full">
          <img
            src={logoImg_N}
            alt="ConnectHub"
            className="w-[380px] xl:w-[440px] h-auto object-contain select-none pointer-events-none drop-shadow-sm"
          />
        </div>

        {/* Ảnh minh họa cộng đồng */}
        <div className="flex items-center justify-center my-auto py-6">
          <img
            src={communityImg}
            alt="Community Illustration"
            className="w-full max-w-[620px] xl:max-w-[720px] h-auto object-contain select-none pointer-events-none drop-shadow-sm"
          />
        </div>

        <div className="h-14 xl:h-16" />
      </div>

      {/* NỬA PHẢI (50%): Nền #ddf3f0 */}
      <div className="w-full lg:w-1/2 min-h-screen bg-[#ddf3f0] flex items-center justify-center p-6 sm:p-10 xl:p-12">
        {/* Lớp bọc Gradient tạo Viền Trái (#a7c8d5) và Viền Phải (#cfefde) */}
        <div className="w-full max-w-[484px] rounded-[38px] p-[3px] bg-gradient-to-r from-[#a7c8d5] to-[#cfefde] shadow-[0_30px_60px_-10px_rgba(0,0,0,0.35),0_10px_20px_-5px_rgba(0,0,0,0.2)]">
          {/* Card Form bên trong */}
          <div className="w-full bg-white rounded-[36px] px-8 sm:px-12 py-10 sm:py-12 flex flex-col justify-center">
            {/* Logo_D trong form */}
            <div className="flex justify-center -mt-4 sm:-mt-6 mb-8 overflow-visible">
              <img
                src={logoImg_D}
                alt="ConnectHub"
                className="w-[280px] sm:w-[320px] h-[160px] object-contain scale-[1.75] select-none pointer-events-none drop-shadow-sm"
              />
            </div>

            {/* Tiêu đề Sign In */}
            <h2 className="text-xl sm:text-2xl font-bold text-slate-800 mb-6 text-left">
              Sign In
            </h2>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
              {/* Field Email / Phone */}
              <div>
                <div className="relative flex items-center">
                  <Mail className="w-5 h-5 absolute left-4 text-slate-400 pointer-events-none" />
                  <input
                    type="text"
                    placeholder="Email / Phone"
                    {...register("phoneOrEmail")}
                    className="w-full pl-12 pr-4 py-3.5 rounded-2xl border border-slate-200 text-sm sm:text-base focus:outline-none focus:border-teal-500 focus:ring-4 focus:ring-teal-500/10 placeholder:text-slate-400 text-slate-800 transition bg-white"
                  />
                </div>
                {errors.phoneOrEmail && (
                  <p className="text-xs text-rose-500 mt-1.5 ml-2">
                    {errors.phoneOrEmail.message}
                  </p>
                )}
              </div>

              {/* Field Password */}
              <div>
                <div className="relative flex items-center">
                  <Lock className="w-5 h-5 absolute left-4 text-slate-400 pointer-events-none" />
                  <input
                    type={showPassword ? "text" : "password"}
                    placeholder="Password"
                    {...register("password")}
                    className="w-full pl-12 pr-12 py-3.5 rounded-2xl border border-slate-200 text-sm sm:text-base focus:outline-none focus:border-teal-500 focus:ring-4 focus:ring-teal-500/10 placeholder:text-slate-400 text-slate-800 transition bg-white"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-4 text-slate-400 hover:text-slate-600 focus:outline-none"
                  >
                    {showPassword ? (
                      <EyeOff className="w-5 h-5" />
                    ) : (
                      <Eye className="w-5 h-5" />
                    )}
                  </button>
                </div>
                {errors.password && (
                  <p className="text-xs text-rose-500 mt-1.5 ml-2">
                    {errors.password.message}
                  </p>
                )}
              </div>

              {/* Nút Gradient SIGN IN */}
              <div className="pt-2">
                <GradientButton type="submit" isLoading={isLoggingIn}>
                  {isLoggingIn ? "Signing In..." : "SIGN IN"}
                </GradientButton>
              </div>
            </form>

            {/* Forgot Password */}
            <div className="text-center mt-4">
              <a
                href="#forgot"
                className="text-xs sm:text-sm text-slate-500 hover:text-slate-700 transition"
              >
                Forgot Password?
              </a>
            </div>

            {/* Create account */}
            <p className="text-center text-xs sm:text-sm text-slate-500 mt-6">
              Don't have an account?{" "}
              <a
                href="/register"
                className="text-[#0284C7] font-semibold hover:underline"
              >
                Sign Up
              </a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
