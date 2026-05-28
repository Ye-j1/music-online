// Three.js animated vinyl/music particle background
(function () {
  var canvas = document.getElementById('three-canvas');
  if (!canvas || typeof THREE === 'undefined') return;

  var renderer = new THREE.WebGLRenderer({ canvas: canvas, alpha: true, antialias: true });
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.setSize(window.innerWidth, window.innerHeight);

  var scene  = new THREE.Scene();
  var camera = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight, 0.1, 1000);
  camera.position.z = 30;

  var particles = [];
  var geometry = new THREE.BufferGeometry();
  var count = 120;
  var positions = new Float32Array(count * 3);

  for (var i = 0; i < count; i++) {
    positions[i * 3]     = (Math.random() - 0.5) * 80;
    positions[i * 3 + 1] = (Math.random() - 0.5) * 60;
    positions[i * 3 + 2] = (Math.random() - 0.5) * 40;
    particles.push({
      vx: (Math.random() - 0.5) * 0.02,
      vy: (Math.random() - 0.5) * 0.02,
      vz: (Math.random() - 0.5) * 0.01
    });
  }

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
  var material = new THREE.PointsMaterial({ color: 0xce93d8, size: 0.5, transparent: true, opacity: 0.5 });
  var points = new THREE.Points(geometry, material);
  scene.add(points);

  function animate() {
    requestAnimationFrame(animate);
    var pos = geometry.attributes.position.array;
    for (var j = 0; j < count; j++) {
      pos[j * 3]     += particles[j].vx;
      pos[j * 3 + 1] += particles[j].vy;
      pos[j * 3 + 2] += particles[j].vz;
      if (Math.abs(pos[j * 3])     > 40) particles[j].vx *= -1;
      if (Math.abs(pos[j * 3 + 1]) > 30) particles[j].vy *= -1;
      if (Math.abs(pos[j * 3 + 2]) > 20) particles[j].vz *= -1;
    }
    geometry.attributes.position.needsUpdate = true;
    points.rotation.y += 0.0005;
    renderer.render(scene, camera);
  }
  animate();

  window.addEventListener('resize', function () {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
  });
})();
