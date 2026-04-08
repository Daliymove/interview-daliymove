/**
 * 路由配置验证脚本
 * 检查所有必需的页面组件文件是否存在
 */

const fs = require('fs');
const path = require('path');

const pagesToCheck = [
  { file: 'frontend/src/views/resume/index.vue', name: 'Resume List' },
  { file: 'frontend/src/views/resume/Upload.vue', name: 'Resume Upload' },
  { file: 'frontend/src/views/resume/Detail.vue', name: 'Resume Detail' },
  { file: 'frontend/src/views/interview/index.vue', name: 'Interview History' },
  { file: 'frontend/src/views/interview/Session.vue', name: 'Interview Session' },
  { file: 'frontend/src/views/knowledge/index.vue', name: 'Knowledge List' },
  { file: 'frontend/src/views/knowledge/Upload.vue', name: 'Knowledge Upload' },
  { file: 'frontend/src/views/knowledge/Query.vue', name: 'Knowledge Query' }
];

console.log('验证路由配置...\n');

let allPassed = true;

pagesToCheck.forEach(page => {
  const fullPath = path.join(__dirname, page.file);
  const exists = fs.existsSync(fullPath);
  
  if (exists) {
    console.log(`✓ ${page.name}: ${page.file}`);
  } else {
    console.log(`✗ ${page.name}: ${page.file} - 文件不存在`);
    allPassed = false;
  }
});

console.log('\n' + (allPassed ? '✓ 所有页面文件检查通过' : '✗ 存在缺失的页面文件'));

// 检查路由配置文件
const routerFile = path.join(__dirname, 'frontend/src/router/index.ts');
if (fs.existsSync(routerFile)) {
  console.log('✓ 路由配置文件存在');
  
  const content = fs.readFileSync(routerFile, 'utf8');
  
  // 检查是否包含所有页面映射
  const requiredMappings = [
    'resume/Upload',
    'resume/Detail',
    'interview/Session',
    'knowledge/index',
    'knowledge/Upload',
    'knowledge/Query'
  ];
  
  console.log('\n检查路由映射配置:');
  requiredMappings.forEach(mapping => {
    if (content.includes(`'${mapping}'`)) {
      console.log(`  ✓ ${mapping}`);
    } else {
      console.log(`  ✗ ${mapping} - 未配置`);
      allPassed = false;
    }
  });
} else {
  console.log('✗ 路由配置文件不存在');
  allPassed = false;
}

// 检查SQL迁移文件
const sqlFile = path.join(__dirname, 'backend/sql/menu_migration.sql');
if (fs.existsSync(sqlFile)) {
  console.log('\n✓ SQL迁移文件存在: backend/sql/menu_migration.sql');
} else {
  console.log('\n✗ SQL迁移文件不存在');
  allPassed = false;
}

console.log('\n================================');
console.log(allPassed ? '验证结果: ✓ 通过' : '验证结果: ✗ 失败');
console.log('================================\n');

if (!allPassed) {
  process.exit(1);
}