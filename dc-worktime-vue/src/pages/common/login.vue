<template>
	<div class="page-login">
		<img src="../../assets/login/login_top_bg.png" class="login_top_bg" alt="" srcset="" />
		<div class="login-container">
			<img w="100px" h="100px" class="login-logo" src="../../assets/logo.png" alt="" />
			<span class="login-title m-t-32px m-b-32px">项目管理与工时分析系统</span>
			<BaseForm v-bind="form" v-model:value="form.value" />
			<div w="100%" h="55px" class="m-t-26px">
				<ElButton :loading="isLoading" type="primary" w="100%" h="55px" @click="handleLogin">登录</ElButton>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import BaseForm from "@/core/struct/form/base-form";
	import { useForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";
	import type { FormInstance } from "element-plus";
	import { ref } from "vue";
	let formInstance = ref<FormInstance>();
	const isLoading = ref(false);
	const { form } = useForm({
		span: 24,
		expand: false,
		inline: true,
		size: "large",
		inlineMessage: false,
		showMessage: true,
		formItems: [
			{
				prop: "username",
				value: "",
				type: "ElInput",
				attrs: {
					placeholder: "请输入用户名",
					onKeydown: (event: any) => {
						console.log(event.keyCode);

						if (event.keyCode == 13) {
							handleLogin();
						}
					}
				}
			},
			{
				prop: "password",
				value: "",
				type: "ElInput",
				attrs: {
					placeholder: "请输入密码",
					type: "password",
					showPassword: true,
					onKeydown: (event: any) => {
						if (event.keyCode == 13) {
							handleLogin();
						}
					}
				}
			}
		],
		rules: {
			username: [{ required: true, message: "请输入", trigger: "blur" }],
			password: [{ required: true, message: "请输入", trigger: "blur" }]
		},
		onInstance(instance) {
			formInstance.value = instance;
		}
	});

	const handleLogin = () => {
		// 存储密码到 localStorage
		localStorage.setItem("lastLoginPassword", form.value.password);
		formInstance.value?.validate().then(async (res) => {
			if (res) {
				try {
					isLoading.value = true;
					await appInstance.actionsLogin(form.value);
					isLoading.value = false;
				} catch (e) {
					isLoading.value = false;
				}
			}
		});
	};
</script>

<style lang="scss" scoped>
	.page-login {
		width: 100vw;
		height: 100vh;
		overflow: hidden;
		background-image: url(../../assets/login/login-bg.jpg);
		background-size: cover;
		position: relative;
	}
	.login_top_bg {
		position: fixed;
		width: 946px;
		height: 772px;
		margin: auto 0;
		top: 0;
		bottom: 0;
		left: 338px;
	}
	.login-container {
		padding: 60px;
		width: 550px;
		height: 547px;
		background: #ffffff;
		box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.12);
		border-radius: 20px 20px 20px 20px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		position: absolute;
		margin: auto 0;
		top: 0;
		bottom: 0;
		right: 338px;
		display: flex;
		flex-direction: column;

		justify-content: center;
		align-items: center;
		.login-title {
			font-family:
				Alimama ShuHeiTi,
				Alimama ShuHeiTi;
			font-weight: bold;
			font-size: 32px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 32px;
		}
	}
</style>
