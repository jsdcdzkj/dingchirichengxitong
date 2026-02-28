// import { Button, Form } from ;

// import { defineComponent, provide, reactive, ref, Ref, VNode } from "vue";
// import { bool, string } from "vue-types";
// import { JSX } from "vue/jsx-runtime";
// import { useDrawerContainer } from "./style";
// import { CloseOutlined } from "@ant-design/icons-vue";

// type DoneProp = () => void | Promise<any>;

// type ApplyFuncItem = <T>(names?: any, options?: any) => Promise<T>;
// type FormInfoConfig = {
// 	modelRef: Props | Ref<Props>;
// 	rulesRef: Props | Ref<Props>;
// 	initialModel: Props;
// 	validateInfos: any;
// 	resetFields: (newValues?: Props) => void;
// 	validate: <T = any>(names?: string | string[], option?: any) => Promise<T>;
// 	// /** This is an internal usage. Do not use in your prod */
// 	validateField: (
// 		name: string,
// 		value: any,
// 		rules: Record<string, unknown>[],
// 		option?: any
// 	) => Promise<any[]>;
// 	// mergeValidateInfo: (items: ValidateInfo | ValidateInfo[]) => ValidateInfo;
// 	clearValidate: (names?: string | string[]) => void;
// };

// const initProps = () => ({
// 	showCancel: bool().def(true),
// 	showConfirm: bool().def(true),
// 	heihgt: string().def("100%")
// });
// const empty = () => {};

// export type drawerProvide = {
// 	bindFormConfig: (
// 		model: Props | Ref<Props>,
// 		rules: Props | Ref<Props>
// 	) => FormInfoConfig;
// 	bindFormDone: (done: DoneProp) => any;
// };
// export interface Props {
// 	[key: string]: any;
// }

// export default defineComponent({
// 	props: initProps(),
// 	emits: ["cancel", "confirm"],
// 	setup(props, { slots, emit }) {
// 		const applayFunc = ref<ApplyFuncItem[]>([]);
// 		const loading = ref(false);
// 		const wrapper = useDrawerContainer("drawer-container");
// 		const pFormData = reactive<{
// 			applayFunc: ApplyFuncItem[];
// 			loading: boolean;
// 			done: DoneProp;
// 		}>({
// 			applayFunc: [],
// 			loading: false,
// 			done: empty
// 		});

// 		const bindFormConfig = (
// 			model: Props | Ref<Props>,
// 			rules: Props | Ref<Props>
// 		) => {
// 			const form = Form.useForm(model, rules);
// 			applayFunc.value.push(form.validate);
// 			return form;
// 		};

// 		const bindFormDone = (done: DoneProp) => {
// 			pFormData.done = done;
// 		};

// 		const handlerCancel = () => {
// 			if (loading.value) return;
// 			emit("cancel");
// 		};

// 		const handlerConfirm = async () => {
// 			if (applayFunc.value.length > 0) {
// 				loading.value = !loading.value;
// 				try {
// 					// 校验表单
// 					const runAll = await Promise.all(
// 						applayFunc.value.map((func) => func())
// 					);

// 					// 校验成功
// 					if (runAll.every((item) => item)) {
// 						// 主体内容的提交事件
// 						emit("confirm", await pFormData.done());
// 					}
// 				} catch {
// 					loading.value = !loading.value;
// 				} finally {
// 					loading.value = false;
// 				}
// 			} else {
// 				emit("confirm");
// 			}
// 		};

// 		// 向子组件传递绑定函数
// 		provide("drawer", { bindFormConfig, bindFormDone });
// 		return () => {
// 			const container = slots.default?.() as unknown as VNode | null;
// 			return wrapper(
// 				<div>
// 					<Close onCancel={handlerCancel} />
// 					<Content content={container}></Content>
// 					{props.showCancel || props.showConfirm ? (
// 						<Footer
// 							showCancel={props.showCancel}
// 							showConfirm={props.showConfirm}
// 							loading={loading.value}
// 							onCancel={handlerCancel}
// 							onConfirm={handlerConfirm}
// 						></Footer>
// 					) : null}
// 				</div>
// 			);
// 		};
// 	}
// });
// // 关闭弹框
// const Close = (props: { onCancel: (e: MouseEvent) => void }) => {
// 	return (
// 		<CloseOutlined
// 			class="drawer-container-close"
// 			onClick={props.onCancel}
// 		/>
// 	);
// };
// // 弹框主体内容
// const Content = (props: { content: VNode | null }) => {
// 	return <div class="drawer-container-main">{props.content}</div>;
// };

// type footer = (props: {
// 	showCancel: boolean;
// 	showConfirm: boolean;
// 	loading: boolean;
// 	onCancel: (e: MouseEvent) => void;
// 	onConfirm: (e: MouseEvent) => void;
// }) => JSX.Element;
// const Footer: footer = (props) => {
// 	// 取消按钮
// 	const cancel = props.showCancel ? (
// 		<Button
// 			type="default"
// 			key="cancel"
// 			loading={props.loading}
// 			onClick={props.onCancel}
// 		>
// 			取消
// 		</Button>
// 	) : null;
// 	// 确认按钮
// 	const confirm = props.showConfirm ? (
// 		<Button
// 			type="primary"
// 			loading={props.loading}
// 			key="confirm"
// 			onClick={props.onConfirm}
// 		>
// 			确认
// 		</Button>
// 	) : null;
// 	return <div class="drawer-container-footer">{[cancel, confirm]}</div>;
// };

import { defineComponent, inject, onMounted, provide, reactive, ref, type Ref, type VNode } from "vue";
import { array, bool, string } from "vue-types";
import type { JSX } from "vue/jsx-runtime";
import { Close as ElClise } from "@element-plus/icons-vue";
import { ElButton, ElIcon, ElCheckbox, type FormInstance, ElDivider } from "element-plus";
import { ProviderSy } from "./dialog-container";
import { debounce } from "lodash-es";

type DoneProp = (type: number) => any | Promise<any>;

type ApplyFuncItem = <T>(names?: any, options?: any) => Promise<T>;

const initProps = () => ({
	showCancel: bool().def(true),
	showConfirm: bool().def(true),
	showZanCun: bool().def(false),
	nextNotRest: array<string>().def([]),
	heihgt: string().def("100%"),
	showNext: bool().def(false)
});
const empty = () => {};

export default defineComponent({
	props: initProps(),
	emits: ["cancel", "confirm", "zancun"],
	setup(props, { slots, emit }) {
		const applayFunc: FormInstance[] = [];
		const loading = ref(false);
		const isNext = ref(true);
		const pFormData = reactive<{
			applayFunc: ApplyFuncItem[];
			loading: boolean;
			done: DoneProp;
		}>({
			applayFunc: [],
			loading: false,
			done: empty
		});

		const registerFormInstrance = (instance: FormInstance) => {
			applayFunc.push(instance);
		};

		const registerFormDone = (done: DoneProp) => {
			pFormData.done = done;
		};

		const handlerCancel = () => {
			if (loading.value) return;
			emit("cancel");
		};

		const handlerConfirm = async () => {
			if (loading.value) return;
			loading.value = true; // 立即上锁

			try {
				if (applayFunc.length > 0) {
					const runAll = await Promise.all(applayFunc.map((func) => func.validate()));
					if (!runAll.every(Boolean)) throw new Error("验证失败");

					const result = await pFormData.done(1); // 确保完全等待异步完成

					if (props.showNext && isNext.value) {
						applayFunc.forEach((func) => {
							if (props.nextNotRest.length == 0) {
								func.resetFields();
							} else {
								const list: string[] = [];
								func.fields.forEach((field) => {
									if (!props.nextNotRest.includes(field.prop as string)) {
										list.push(field.prop as string);
									}
								});
								func.resetFields(list);
							}
						});
					} else {
						emit("confirm", result);
					}
				} else {
					emit("confirm");
				}
			} catch (e) {
				console.error("操作失败:", e);
			} finally {
				loading.value = false; // 统一在此解锁
			}
		};

		const handlerZanCun = async () => {
			if (loading.value) return;
			loading.value = true; // 立即上锁

			try {
				if (applayFunc.length > 0) {
					const runAll = await Promise.all(applayFunc.map((func) => func.validate()));
					if (!runAll.every(Boolean)) throw new Error("验证失败");

					const result = await pFormData.done(2); // 确保完全等待异步完成

					if (props.showNext && isNext.value) {
						applayFunc.forEach((func) => {
							if (props.nextNotRest.length == 0) {
								func.resetFields();
							} else {
								const list: string[] = [];
								func.fields.forEach((field) => {
									if (!props.nextNotRest.includes(field.prop as string)) {
										list.push(field.prop as string);
									}
								});
								func.resetFields(list);
							}
						});
					} else {
						emit("zancun", result);
					}
				} else {
					emit("zancun");
				}
			} catch (e) {
				console.error("操作失败:", e);
			} finally {
				loading.value = false; // 统一在此解锁
			}
		};
		const debouncedConfirm = debounce(handlerConfirm, 500, { leading: true, trailing: false });
		// 暂存
		const debouncedZanCun = debounce(handlerZanCun, 500, { leading: true, trailing: false });
		// 向子组件传递绑定函数
		provide(ProviderSy, { registerFormInstrance, registerFormDone });

		return () => {
			const container = slots.default?.() as unknown as VNode | null;
			return (
				<div class="drawer-container">
					{/* <Close onCancel={handlerCancel} /> */}
					<Content content={container}></Content>
					<ElDivider class="m-t-0px m-b-0px" />
					<Footer
						showCancel={props.showCancel}
						showConfirm={props.showConfirm}
						showNext={props.showNext}
						showZanCun={props.showZanCun}
						isNext={isNext}
						loading={loading.value}
						onCancel={handlerCancel}
						onConfirm={debouncedConfirm}
						onZanCun={debouncedZanCun}
					></Footer>
				</div>
			);
		};
	}
});
// // 关闭弹框
// const Close = (props: { onCancel: (e: MouseEvent) => void }) => {
// 	return (
// 		<span onClick={props.onCancel}>
// 			<ElIcon class="drawer-container-close">
// 				<ElClise />
// 			</ElIcon>
// 		</span>
// 	);
// };
// 弹框主体内容
const Content = (props: { content: VNode | null }) => {
	return <div class="drawer-container-main">{props.content}</div>;
};

type footer = (props: {
	showCancel: boolean;
	showConfirm: boolean;
	showNext: boolean;
	showZanCun: boolean;
	isNext: Ref<boolean>;
	loading: boolean;
	onCancel: (e: MouseEvent) => void;
	onConfirm: (e: MouseEvent) => void;
	onZanCun: (e: MouseEvent) => void;
}) => JSX.Element;
const Footer: footer = (props) => {
	// 取消按钮
	const cancel = props.showCancel ? (
		<ElButton type="default" key="cancel" loading={props.loading} onClick={props.onCancel}>
			取消
		</ElButton>
	) : null;
	// 确认按钮
	const confirm = props.showConfirm ? (
		<ElButton type="primary" loading={props.loading} key="confirm" onClick={props.onConfirm}>
			确认
		</ElButton>
	) : null;

	const zancun = props.showZanCun ? (
		<ElButton type="primary" loading={props.loading} key="confirm" onClick={props.onZanCun}>
			暂存
		</ElButton>
	) : null;
	if (zancun == null && confirm == null && confirm == null) return null;
	return (
		<div class="drawer-container-footer">
			<div>
				{props.showNext ? (
					<ElCheckbox v-model:modelValue={props.isNext.value}>是否添加下一个</ElCheckbox>
				) : null}
			</div>
			<div>{[cancel, zancun, confirm]}</div>
		</div>
	);
};
