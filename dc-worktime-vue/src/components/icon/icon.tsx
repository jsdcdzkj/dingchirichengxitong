import type { CSSProperties } from "vue";

// 导出一个名为 Icon 的函数组件
export function Icon(props: { name: string; size: CSSProperties }) {
	// 返回一个 img 元素，用于显示图标
	return (
		<img
			// 设置 img 元素的 src 属性，值为 `/icon/${props.name}`，其中 `props.name` 是传入的图标名称
			src={`/icon/${props.name}`}
			// 设置 img 元素的 style 属性，用于控制图标的大小
			style={{
				// 设置 img 元素的宽度，值为 `props.size.width`，其中 `props.size.width` 是传入的宽度值
				width: props.size.width,
				// 设置 img 元素的高度，值为 `props.size.height`，其中 `props.size.height` 是传入的高度值
				height: props.size.height
			}}
		/>
	);
}

// 导出一个名为 ProjectInfoIcon 的函数组件
export function ProjectInfoIcon(props: {
	// 定义组件的属性接口
	name: string; // 图标名称，必填
	width?: number; // 图标宽度，可选
	height?: number; // 图标高度，可选
	size?: number; // 图标大小，可选，优先级高于宽度和高度
	cssStyle?: CSSProperties; // 自定义CSS样式，可选
}) {
	// 返回一个 img 标签，用于显示图标
	return (
		<img
			// 设置 img 标签的 src 属性，图标路径基于 name 属性动态生成
			src={`/icon/project-info/${props.name}`}
			// 设置 img 标签的 style 属性，动态计算宽度和高度
			// 如果 size 属性存在，则使用 size 作为宽度和高度
			// 否则，使用 width 和 height 属性，如果它们也不存在，则默认为 16px
			style={{
				width: `${props.size || props.width || 16}px`,
				height: `${props.size || props.height || 16}px`,
				// 将传入的 cssStyle 属性展开并应用到 img 标签上
				...props.cssStyle
			}}
		/>
	);
}

export const customIcons = import.meta.glob("../../../public/icon/**/*.*");

export const getIconsName = () => Object.keys(customIcons).map((item) => item.match(/[^\/\\]+(?=\.\w+$)/)?.[0] || "");

export const getIconsNameExt = () => Object.keys(customIcons).map((item) => item.match(/[^\/\\]+$/)?.[0] || "");

export const Icons = [
	"icon-dev.png",
	"icon-home-bumen.png",
	"icon-leader-renwu.png",
	"icon-menu.png",
	"icon-renwukanban.png",
	"icon-history.png",
	"icon-meeting.png",
	"icon-role.png",
	"icon-test-mange.png",
	"icon-test-plan.png",
	"icon-user.png",
	"icon-worker.png",
	"icon-yunwei.png",
	"projec-gongneng.png",
	"projecct-wu.png",
	"project-history.png",
	"project-info.png",
	"project-lin.png",
	"project-list.png",
	"project-plan.png",
	"project-progress.png",
	"project-wuqiu.png"
];
