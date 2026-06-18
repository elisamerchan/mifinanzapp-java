const fs = require('fs');
const path = require('path');

const vendorDir = path.join(__dirname, '..', 'src', 'main', 'resources', 'static', 'vendor');

const filesToCopy = [
    {
        src: path.join(__dirname, '..', 'node_modules', 'bootstrap', 'dist', 'css', 'bootstrap.min.css'),
        dest: path.join(vendorDir, 'bootstrap.min.css')
    },
    {
        src: path.join(__dirname, '..', 'node_modules', 'bootstrap', 'dist', 'js', 'bootstrap.bundle.min.js'),
        dest: path.join(vendorDir, 'bootstrap.bundle.min.js')
    },
    {
        src: path.join(__dirname, '..', 'node_modules', 'chart.js', 'dist', 'chart.umd.min.js'),
        dest: path.join(vendorDir, 'chart.umd.min.js')
    }
];

if (!fs.existsSync(vendorDir)) {
    fs.mkdirSync(vendorDir, { recursive: true });
}

filesToCopy.forEach(file => {
    if (fs.existsSync(file.src)) {
        fs.copyFileSync(file.src, file.dest);
        console.log(`Copied: ${file.src} -> ${file.dest}`);
    } else {
        console.error(`Source not found: ${file.src}`);
    }
});

console.log('Vendor files copied successfully!');
