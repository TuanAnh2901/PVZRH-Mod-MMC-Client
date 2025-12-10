<template>
<el-drawer
    :title="addFlag ? '添加' : '编辑'"
    :size="500"
    v-model="visibleFlag"
    :before-close="onClose"
>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
    <el-form-item label="版本号" prop="versionNumber" >
      <el-input v-model="form.versionNumber" placeholder="版本号"/>
    </el-form-item>
    <el-form-item label="版本描述" prop="versionDescription" >
      <el-input v-model="form.versionDescription" placeholder="版本描述"/>
    </el-form-item>
    <el-form-item label="版本更新内容" prop="updateContent" >
      <el-input type="textarea" v-model="form.updateContent"
                placeholder="版本更新内容"/>
    </el-form-item>
    <el-form-item label="版本下载地址" prop="downloadUrl" >
      <el-input v-model="form.downloadUrl" placeholder="版本下载地址"/>
    </el-form-item>
    <el-form-item label="是否发布" prop="isReleased" >
      <el-switch v-model="form.isReleased" :active-value="true"/>
    </el-form-item>
    <el-form-item label="创建时间" prop="createdTime"  v-if="false">
      <el-date-picker v-model="form.createdTime" type="datetime"
                      placeholder="创建时间"
                      format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>
    <el-form-item label="更新时间" prop="updatedTime"  v-if="false">
      <el-date-picker v-model="form.updatedTime" type="datetime"
                      placeholder="更新时间"
                      format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>
  </el-form>

  <div class="drawer-footer">
    <el-button @click="onClose">取消</el-button>
    <el-button type="primary" @click="onSubmit">保存</el-button>
  </div>
</el-drawer>
</template>
<script setup>
import {reactive, ref, nextTick} from 'vue';
import _ from 'lodash';
import {ElMessage} from 'element-plus';
import {clientVersionApi} from '@/api/client-version-api';
// ------------------------ 联表查询VO ------------------------
const queryFormState = {
  pageNum: 1,
  pageSize: 10,
  sortItemList: []
}

// ------------------------ 中间表更新vo ------------------------
// ------------------------ 枚举量 ------------------------



// ------------------------ 图片上传 ------------------------
// ------------------------ 事件 ------------------------

const emits = defineEmits(['reloadList']);

// ------------------------ 显示与隐藏 ------------------------
// 是否显示
const visibleFlag = ref(false);
// 是否新增
const addFlag = ref(false);

function show(rowData) {
  Object.assign(form, formDefault);
  if (rowData && !_.isEmpty(rowData)) {
    Object.assign(form, rowData);
  }
  visibleFlag.value = true;
  addFlag.value = rowData.id == null;
  nextTick(() => {
    formRef.value.clearValidate();
  });
}

function onClose() {
  Object.keys(form).forEach(key => form[key] = null);
  visibleFlag.value = false;
}

// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref();

const formDefault = {
  id: undefined, // ID
  versionNumber: undefined, // 版本号
  versionDescription: undefined, // 版本描述
  updateContent: undefined, // 版本更新内容
  downloadUrl: undefined, // 版本下载地址
  isReleased: undefined, // 是否发布
  createdTime: undefined, // 创建时间
  updatedTime: undefined, // 更新时间
};

let form = reactive({...formDefault});

const rules = {
  versionNumber: [{
    required: true,
    message: '版本号 必填',
    trigger: 'blur'
  }],
};

// 点击确定，验证表单
async function onSubmit() {
  try {
    await formRef.value.validate();
    save();
  } catch (err) {
    ElMessage.error('参数验证错误，请仔细填写表单数据!');
  }
}

// 新建、编辑API
async function save() {
  try {
    if (addFlag.value) {
      await clientVersionApi.add(form);
    } else {
      await clientVersionApi.update(form);
    }
    ElMessage.success('操作成功');
    emits('reloadList');
    onClose();
  } catch (err) {
    console.log(err)
  }
}

defineExpose({
  show,
});
</script>
<style scoped lang="scss">
.pagination {
  display: flex;
  flex-direction: column;
  align-items: center;
}


</style>