import { z } from "zod";

export const loginSchema = z.object({
  phoneOrEmail: z
    .string({ message: "Vui lòng nhập email hoặc số điện thoại" })
    .min(1, "Vui lòng nhập email hoặc số điện thoại"),

  password: z
    .string({ message: "Vui lòng nhập mật khẩu" })
    .min(1, "Vui lòng nhập mật khẩu"),
});

export type LoginFormData = z.infer<typeof loginSchema>;