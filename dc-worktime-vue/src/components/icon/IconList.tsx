import { defineComponent, h, markRaw, ref } from "vue";
import * as ElementIcons from "@element-plus/icons-vue";
import { ElDivider, ElIcon } from "element-plus";
import { getIconsNameExt, Icon, Icons } from "./icon";

export default defineComponent({
	emits: ["click"],
	setup(_, { emit }) {
		const list = Object.keys(ElementIcons).map((item) => {
			const component = Reflect.get(ElementIcons, item);
			return {
				name: component.name,
				component: markRaw(component)
			};
		});
		const customList = getIconsNameExt().map((icon) => {
			return {
				name: icon,
				component: (
					<Icon
						name={icon}
						size={{ width: "16px", height: "16px" }}
					></Icon>
				)
			};
		});

		return () => (
			<div class="flex flex-wrap gap-5px" w="100%">
				<span>基本</span>
				<ElDivider class="m-0" />
				{list.map((item) => {
					return (
						<div
							w="16px"
							h="16px"
							onClick={() => emit("click", item.name)}
						>
							<ElIcon
								size="16px"
								style="color:var(--el-color-primary)"
							>
								{h(item.component)}
							</ElIcon>
						</div>
					);
				})}
				<div w="100%">自定义</div>
				<ElDivider class="m-0" />
				{customList.map((item) => {
					return (
						<div
							w="16px"
							h="16px"
							onClick={() => emit("click", item.name)}
						>
							<ElIcon
								size="16px"
								style="color:var(--el-color-primary)"
							>
								{h(item.component)}
							</ElIcon>
						</div>
					);
				})}
			</div>
		);
	}
});
