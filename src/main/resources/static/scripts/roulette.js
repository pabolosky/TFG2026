const numbers = [0,32,15,19,4,21,2,25,17,34,6,27,13,36,11,30,8,23,10,5,24,16,33,1,20,14,31,9,22,18,29,7,28,12,35,3,26];
const red = [1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36];

const canvas = document.getElementById('roulette-canvas');
const ctx = canvas.getContext('2d');
const sliceAngle = (2*Math.PI) / numbers.length;
const cx = canvas.width / 2, cy = canvas.height /2, r = 136;

function numColour(n) {
    if (n === 0) return '#1a6b2a';
    return red.includes(n) ? '#b52020' : '#111111';
}

function drawWheel(angle) {
    ctx.clearRect(0, 0, canvas.width, canvas.height); 
    ctx.save();
    ctx.translate(cx, cy);
    ctx.rotate(angle);

    for (let i = 0; i < numbers.length; i++){
        const start = i * sliceAngle - sliceAngle / 2;
        const end = start + sliceAngle;
        const n = numbers[i];

        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.arc(0, 0, r, start, end);
        ctx.closePath();
        ctx.fillStyle = numColour(n);
        ctx.fill();
        ctx.strokeStyle = '#c9a84c';
        ctx.stroke();

        ctx.save();
        ctx. rotate(start + sliceAngle / 2);
         ctx.translate(r * 0.73, 0);
        ctx.rotate(Math.PI / 2);
        ctx.fillStyle = '#ffffff';
        ctx.font = 'bold 11px monospace';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(String(n), 0, 0);
        ctx.restore();
    }

    ctx.restore();

    ctx.beginPath();
    ctx.arc(cx, cy, r * 0.33, 0, Math.PI * 2);
    ctx.fillStyle = '#1a0a00';
    ctx.fill();

const puaX = cx;
const puaY = cy + r; 
const puaAncho = 10;
const puaAlto = 22;

ctx.beginPath();
ctx.moveTo(puaX, puaY); 
ctx.lineTo(puaX - puaAncho, puaY + puaAlto); 
ctx.lineTo(puaX + puaAncho, puaY + puaAlto); 
ctx.closePath();
ctx.fillStyle = '#c9a84c';
ctx.fill();
}

drawWheel(0);

document.querySelectorAll('.bet-square').forEach(square => {
    square.addEventListener('click', function() {
        const group = this.closest('.bet-group');
        
        if (this.classList.contains('active')) {
            this.classList.remove('active');
        } else {
            group.querySelectorAll('.bet-square').forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
        }
    });
});

let currentAngle = 0;
let spinning = false;

function easeOut(t) { return 1 - Math.pow(1 - t, 4); }

async function spinWheel() {
    if (spinning) return;

    let apuestas = [];
    // Recolectamos apuestas de los grupos
    document.querySelectorAll('.bet-group').forEach(group => {
        const activeBtn = group.querySelector('.bet-square.active');
        const quantity = parseInt(group.querySelector('.bet-slider').value);
        const category = group.dataset.category;

        if (activeBtn && quantity > 0) {
            apuestas.push({quantity: quantity, betType: activeBtn.id, betValue: activeBtn.dataset.value});
        } 
        else if (category === 'single-number' && quantity > 0) {
            const num = document.getElementById('specific-number').value;
            if (num !== "") {
                apuestas.push({quantity: quantity, betType: "number", betValue: num});
            }
        }
    });

    if (apuestas.length === 0) {
        alert("Debes seleccionar una opción y asignar un monto.");
        return;
    }

    // 1. Bloquear interfaz
    document.getElementById('spin-btn').disabled = true;
    document.getElementById('status-msg').textContent = 'Validando apuesta...';

    // 2. Obtener Tokens CSRF (Asegúrate de tener los <meta> en el HTML)
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    try {
        // 3. Petición POST al servidor
        const response = await fetch('/games/roulette/spin', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                [header]: token 
            },
            body: JSON.stringify({ apuestas: apuestas })
        });

        if (!response.ok) throw new Error("Error en el servidor");

        const data = await response.json();
        const { number, index, colour } = data; // El server debe devolver el 'index' del número

        // 4. Iniciar Animación
        iniciarAnimacion(number, index, colour);

    } catch (error) {
        console.error(error);
        document.getElementById('status-msg').textContent = 'Error al conectar con el casino';
        document.getElementById('spin-btn').disabled = false;
    }
}

function iniciarAnimacion(number, index, colour) {
    spinning = true;
    const startTime = performance.now();
    const duration = 4500;
    const extraSpins = 6 * Math.PI * 2;
    const pointerAngle = Math.PI / 2;
    const targetSliceCenter = index * sliceAngle;
    const finalAngle = extraSpins + pointerAngle - targetSliceCenter;

    function frame(now) {
        const elapsed = now - startTime;
        const t = Math.min(elapsed / duration, 1);
        const angle = finalAngle * easeOut(t);
        
        drawWheel(angle);

        if (t < 1) {
            requestAnimationFrame(frame);
        } else {
            spinning = false;
            document.getElementById('spin-btn').disabled = false;
            document.getElementById('result-number').textContent = number;
            document.getElementById('status-msg').textContent = '¡Cayó en el '+ number + ' ' + colour + '!';
        }
    }
    requestAnimationFrame(frame);
}

document.querySelectorAll('.bet-slider').forEach(slider => {
    slider.addEventListener('input', function() {
        const group = this.closest('.bet-group');
        if (!group) {
            console.error("No se encontró el contenedor .bet-group para este slider");
            return;
        }
        const display = group.querySelector('.amount-display');
        
        if (display) {
            display.textContent = this.value;
            console.log(this.value);
        }
    });
});