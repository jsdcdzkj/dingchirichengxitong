import { ArrowDown, ArrowUp, Refresh, Search } from "@element-plus/icons-vue";
import { ElButton, ElIcon } from "element-plus";
import { computed, defineComponent } from "vue";
import { bool } from "vue-types";

export default defineComponent({
	name: "EExpand",
	props: {
		expand: bool().def(false),
		showExpand: bool().def(false)
	},
	emits: ["search", "reset", "expandChange"],
	setup(props, { emit }) {
		const clsaaRepx = computed(() =>
			props.expand ? "w-100% text-left" : "w-100% text-right"
		);
		return () => (
			<div class={clsaaRepx.value}>
				<ElButton
					icon={Search}
					type="primary"
					onClick={() => emit("search")}
				>
					搜索
				</ElButton>
				<ElButton icon={Refresh} onClick={() => emit("reset")}>
					重置
				</ElButton>
				{/* {props.showExpand ? (
					<a
						class="m-l-10px cursor-pointer"
						onClick={() => emit("expandChange")}
					>
						<ElIcon>
							{!props.expand ? <ArrowUp /> : <ArrowDown />}
						</ElIcon>
						<span>{!props.expand ? "隐藏" : "显示"}</span>
					</a>
				) : null} */}
			</div>
		);
	}
});
