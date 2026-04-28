(function() {
    const menus = [document.getElementById('menu-juegos'), document.getElementById('menu-login')];
    let timeout;
    menus.forEach(menu => {
        if(menu){
            menu.addEventListener('mouseenter', ()=> {
                clearTimeout(timeout);
                menu.classList.add('show-dropdown');
            });
            menu.addEventListener('mouseleave', ()=> {
                timeout = setTimeout(()=> menu.classList.remove('show-dropdown'), 30);
            });
        }
    });
})();