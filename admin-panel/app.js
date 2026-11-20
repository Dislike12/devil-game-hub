// Devil Game Hub - Admin Dashboard Engine

const DEFAULT_CATALOG = {
  version: 1,
  lastUpdated: new Date().toISOString().split('T')[0],
  games: [
    {
      id: "game_2048",
      title: "2048 Cyber",
      description: "Join the neon numbers and reach tile 2048 in this addictive cybernetic puzzle challenge!",
      category: "Puzzle",
      thumbnailUrl: "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
      gameUrl: "file:///android_asset/games/2048/index.html",
      assetPath: "games/2048/index.html",
      type: "2D",
      status: "published",
      isFeatured: true,
      isPopular: true,
      rating: 4.9,
      playsCount: 42100,
      orientation: "portrait",
      version: 1,
      tags: ["Puzzle", "Brain", "Numbers", "Casual", "Offline"]
    },
    {
      id: "game_snake",
      title: "Viper Snake Cyber",
      description: "Control the glowing neon viper, consume cyber energy, and dominate the grid in this classic arcade game!",
      category: "Arcade",
      thumbnailUrl: "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600&auto=format&fit=crop&q=80",
      gameUrl: "file:///android_asset/games/snake/index.html",
      assetPath: "games/snake/index.html",
      type: "2D",
      status: "published",
      isFeatured: true,
      isPopular: true,
      rating: 4.8,
      playsCount: 47300,
      orientation: "portrait",
      version: 1,
      tags: ["Arcade", "Snake", "Reflex", "Neon", "Offline"]
    },
    {
      id: "game_pacman",
      title: "Cyber Pacman",
      description: "Retro classic maze runner game. Eat dots and dodge neon ghosts!",
      category: "Retro",
      thumbnailUrl: "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600&auto=format&fit=crop&q=80",
      gameUrl: "https://pacman-game.netlify.app/",
      assetPath: "",
      type: "HTML5",
      status: "published",
      isFeatured: true,
      isPopular: true,
      rating: 4.9,
      playsCount: 54200,
      orientation: "any",
      version: 1,
      tags: ["Retro", "Arcade", "Classic", "Maze"]
    },
    {
      id: "game_tower",
      title: "Neon Tower Stack",
      description: "Stack neon blocks as high as you can in this precision timing game.",
      category: "Action",
      thumbnailUrl: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
      gameUrl: "https://tower-game.netlify.app/",
      assetPath: "",
      type: "HTML5",
      status: "published",
      isFeatured: false,
      isPopular: true,
      rating: 4.7,
      playsCount: 29800,
      orientation: "portrait",
      version: 1,
      tags: ["Action", "Timing", "Stacking", "Neon"]
    },
    {
      id: "game_breakout",
      title: "Laser Brick Breaker",
      description: "Bounce neon balls to shatter futuristic bricks with laser power-ups!",
      category: "Arcade",
      thumbnailUrl: "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=600&auto=format&fit=crop&q=80",
      gameUrl: "https://html5.gamedistribution.com/rvvASndc95779e504c564bbda9fef4c090e0c345/",
      assetPath: "",
      type: "HTML5",
      status: "published",
      isFeatured: false,
      isPopular: false,
      rating: 4.6,
      playsCount: 21000,
      orientation: "portrait",
      version: 1,
      tags: ["Arcade", "Bricks", "Classic", "Physics"]
    },
    {
      id: "game_cyber_racer",
      title: "Cyber Nitro Racer",
      description: "Futuristic high-speed nitro racing through neon highways.",
      category: "Racing",
      thumbnailUrl: "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600&auto=format&fit=crop&q=80",
      gameUrl: "https://example.com/racing",
      assetPath: "",
      type: "3D",
      status: "draft",
      isFeatured: false,
      isPopular: false,
      rating: 4.5,
      playsCount: 0,
      orientation: "landscape",
      version: 1,
      tags: ["Racing", "3D", "Draft"]
    }
  ],
  categories: [
    { id: "Arcade", name: "Arcade", icon: "🎮" },
    { id: "Puzzle", name: "Puzzle", icon: "🧩" },
    { id: "Retro", name: "Retro", icon: "🕹️" },
    { id: "Action", name: "Action", icon: "🔥" },
    { id: "Racing", name: "Racing", icon: "🏎️" },
    { id: "Strategy", name: "Strategy", icon: "♟️" },
    { id: "Sports", name: "Sports", icon: "⚽" }
  ]
};

let catalog = null;

// Defensive DOM Helper Utilities
function setElemText(id, text) {
  const el = document.getElementById(id);
  if (el) el.innerText = text;
}

function setElemHtml(id, html) {
  const el = document.getElementById(id);
  if (el) el.innerHTML = html;
}

function loadCatalog() {
  try {
    const saved = localStorage.getItem('devil_admin_catalog');
    if (saved) {
      const parsed = JSON.parse(saved);
      if (parsed && Array.isArray(parsed.games) && parsed.games.length > 0) {
        if (!Array.isArray(parsed.categories) || parsed.categories.length === 0) {
          parsed.categories = JSON.parse(JSON.stringify(DEFAULT_CATALOG.categories));
        }
        return parsed;
      }
    }
  } catch (e) {
    console.warn("Failed to load catalog from LocalStorage, falling back to default sample catalog:", e);
  }
  const initial = JSON.parse(JSON.stringify(DEFAULT_CATALOG));
  try {
    localStorage.setItem('devil_admin_catalog', JSON.stringify(initial));
  } catch (_) {}
  return initial;
}

function saveCatalog() {
  if (!catalog) return;
  catalog.lastUpdated = new Date().toISOString().split('T')[0];
  try {
    localStorage.setItem('devil_admin_catalog', JSON.stringify(catalog));
  } catch (_) {}
  renderAllViews();
}

function initDashboard() {
  catalog = loadCatalog();
  loadGitHubSettings();
  renderAllViews();
  bindGlobalEvents();
}

function loadGitHubSettings() {
  const owner = localStorage.getItem('gh_owner') || 'devilgamehub';
  const repo = localStorage.getItem('gh_repo') || 'games-db';
  const branch = localStorage.getItem('gh_branch') || 'main';
  const path = localStorage.getItem('gh_path') || 'games.json';

  const elOwner = document.getElementById('ghOwner');
  const elRepo = document.getElementById('ghRepo');
  const elBranch = document.getElementById('ghBranch');
  const elPath = document.getElementById('ghPath');

  if (elOwner && !elOwner.value) elOwner.value = owner;
  if (elRepo && !elRepo.value) elRepo.value = repo;
  if (elBranch && !elBranch.value) elBranch.value = branch;
  if (elPath && !elPath.value) elPath.value = path;
}

document.addEventListener('DOMContentLoaded', () => {
  initDashboard();
});

window.onload = function() {
  if (!catalog) initDashboard();
};

function bindGlobalEvents() {
  const searchInput = document.getElementById('searchGames');
  if (searchInput) {
    searchInput.removeEventListener('input', renderGamesTable);
    searchInput.addEventListener('input', renderGamesTable);
  }

  const catSelect = document.getElementById('filterCategory');
  if (catSelect) {
    catSelect.removeEventListener('change', renderGamesTable);
    catSelect.addEventListener('change', renderGamesTable);
  }

  const statusSelect = document.getElementById('filterStatus');
  if (statusSelect) {
    statusSelect.removeEventListener('change', renderGamesTable);
    statusSelect.addEventListener('change', renderGamesTable);
  }
}

function renderAllViews() {
  if (!catalog) return;
  populateCategorySelects();
  renderDashboardStats();
  renderRecentGames();
  renderGamesTable();
  renderCategories();
  updateStorageInfo();
}

function showToast(message, type = 'success') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type === 'error' ? 'toast-error' : ''}`;
  toast.innerHTML = `<span>${type === 'error' ? '❌' : '✅'}</span> ${message}`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.remove();
  }, 3200);
}

function switchView(viewName) {
  document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));
  document.querySelectorAll('.view-section').forEach(sec => sec.style.display = 'none');

  const navItem = document.getElementById(`nav-${viewName}`);
  if (navItem) navItem.classList.add('active');

  const viewSec = document.getElementById(`view-${viewName}`);
  if (viewSec) viewSec.style.display = 'block';

  const titles = {
    overview: { title: 'Dashboard Overview', sub: 'Overview of platform statistics, metrics, and quick actions.' },
    games: { title: 'All Registered Games', sub: 'Manage, search, filter, edit, and publish game entries.' },
    categories: { title: 'Category Management', sub: 'Organize games into categories and add new custom genres.' },
    settings: { title: 'Local Storage & GitHub Settings', sub: 'Publish dynamic catalogs to GitHub REST API and manage browser storage.' }
  };

  if (titles[viewName]) {
    setElemText('viewHeaderTitle', titles[viewName].title);
    setElemText('viewHeaderSubtitle', titles[viewName].sub);
  }
}

function handleAddGameNav() {
  switchView('games');
  openAddGameModal();
}

function renderDashboardStats() {
  if (!catalog || !Array.isArray(catalog.games)) return;

  const total = catalog.games.length;
  const published = catalog.games.filter(g => (g.status || 'published') === 'published').length;
  const drafts = catalog.games.filter(g => g.status === 'draft').length;
  const categories = catalog.categories ? catalog.categories.length : 7;

  setElemText('statTotal', total);
  setElemText('statPublished', published);
  setElemText('statDrafts', drafts);
  setElemText('statCategories', categories);
}

function renderRecentGames() {
  const tableBody = document.getElementById('recentGamesTableBody');
  if (!tableBody || !catalog || !Array.isArray(catalog.games)) return;
  tableBody.innerHTML = '';

  const recents = catalog.games.slice(0, 5);
  recents.forEach(game => {
    const status = game.status || 'published';
    const isPub = status === 'published';
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${game.title}</strong><br><small style="color:var(--text-muted);">${game.id}</small></td>
      <td><span style="color:var(--neon-purple); font-weight:bold;">${game.category}</span></td>
      <td>${game.type || 'HTML5'}</td>
      <td><span class="status-tag ${isPub ? 'status-published' : 'status-draft'}">${status.toUpperCase()}</span></td>
      <td>
        <button class="btn btn-secondary" style="padding:4px 8px; font-size:10px;" onclick="openEditGameModal('${game.id}')">Edit</button>
      </td>
    `;
    tableBody.appendChild(tr);
  });
}

function renderGamesTable() {
  const tableBody = document.getElementById('gamesTableBody');
  if (!tableBody || !catalog || !Array.isArray(catalog.games)) return;

  const searchEl = document.getElementById('searchGames');
  const catEl = document.getElementById('filterCategory');
  const statusEl = document.getElementById('filterStatus');

  const searchVal = (searchEl ? searchEl.value : '').toLowerCase().trim();
  const catVal = catEl ? catEl.value : 'all';
  const statusVal = statusEl ? statusEl.value : 'all';

  tableBody.innerHTML = '';

  const filtered = catalog.games.filter(game => {
    const matchesSearch = !searchVal ||
      (game.title && game.title.toLowerCase().includes(searchVal)) ||
      (game.id && game.id.toLowerCase().includes(searchVal)) ||
      (game.category && game.category.toLowerCase().includes(searchVal));

    const matchesCat = !catVal || catVal === 'all' || (game.category && game.category.toLowerCase() === catVal.toLowerCase());
    const matchesStatus = !statusVal || statusVal === 'all' || (game.status || 'published') === statusVal;

    return matchesSearch && matchesCat && matchesStatus;
  });

  if (filtered.length === 0) {
    tableBody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 24px;">No games match the current search or filters.</td></tr>`;
    return;
  }

  filtered.forEach(game => {
    const status = game.status || 'published';
    const isPub = status === 'published';
    const tr = document.createElement('tr');

    tr.innerHTML = `
      <td><strong>${game.title}</strong><br><small style="color:var(--text-muted);">${game.id}</small></td>
      <td><span style="color:var(--neon-purple); font-weight:bold;">${game.category}</span></td>
      <td>${game.type || 'HTML5'}</td>
      <td>⭐ ${game.rating || 4.5}</td>
      <td>
        <span class="status-tag ${isPub ? 'status-published' : 'status-draft'}">
          ${status.toUpperCase()}
        </span>
      </td>
      <td>${game.isFeatured ? '🔥 Featured' : ''} ${game.isPopular ? '⭐ Popular' : ''}</td>
      <td>
        <div style="display:flex; gap:6px;">
          <button class="btn btn-secondary" style="padding:4px 8px; font-size:10px;" onclick="toggleGameStatus('${game.id}')">${isPub ? 'Unpublish' : 'Publish'}</button>
          <button class="btn btn-secondary" style="padding:4px 8px; font-size:10px;" onclick="openEditGameModal('${game.id}')">Edit</button>
          <button class="btn btn-danger" style="padding:4px 8px; font-size:10px;" onclick="deleteGame('${game.id}')">Delete</button>
        </div>
      </td>
    `;
    tableBody.appendChild(tr);
  });
}

function toggleGameStatus(id) {
  const game = catalog.games.find(g => g.id === id);
  if (game) {
    game.status = game.status === 'published' ? 'draft' : 'published';
    saveCatalog();
    showToast(`Game '${game.title}' status changed to ${game.status.toUpperCase()}`);
  }
}

function openAddGameModal() {
  setElemText('modalTitle', 'Add New Game');
  const editIdEl = document.getElementById('editGameId');
  if (editIdEl) editIdEl.value = '';

  const formEl = document.getElementById('gameForm');
  if (formEl) formEl.reset();

  const gameIdEl = document.getElementById('gameId');
  if (gameIdEl) {
    gameIdEl.value = 'game_' + Date.now();
    gameIdEl.disabled = false;
  }

  const modalEl = document.getElementById('gameModal');
  if (modalEl) modalEl.classList.add('active');
}

function openEditGameModal(id) {
  const game = catalog.games.find(g => g.id === id);
  if (!game) return;

  setElemText('modalTitle', 'Edit Game');

  const editIdEl = document.getElementById('editGameId');
  if (editIdEl) editIdEl.value = game.id;

  const gameIdEl = document.getElementById('gameId');
  if (gameIdEl) {
    gameIdEl.value = game.id;
    gameIdEl.disabled = true;
  }

  const fields = {
    gameTitle: game.title || '',
    gameDesc: game.description || '',
    gameCategory: game.category || 'Arcade',
    gameType: game.type || '2D',
    gameStatus: game.status || 'published',
    gameOrientation: game.orientation || 'portrait',
    gameAssetPath: game.assetPath || '',
    gameUrl: game.gameUrl || '',
    gameThumb: game.thumbnailUrl || '',
    gameRating: game.rating || 4.8,
    gameFeatured: game.isFeatured ? 'true' : 'false',
    gamePopular: game.isPopular ? 'true' : 'false'
  };

  for (const [key, val] of Object.entries(fields)) {
    const el = document.getElementById(key);
    if (el) el.value = val;
  }

  const modalEl = document.getElementById('gameModal');
  if (modalEl) modalEl.classList.add('active');
}

function closeModal() {
  const modalEl = document.getElementById('gameModal');
  if (modalEl) modalEl.classList.remove('active');
}

function saveGameForm(e) {
  if (e) e.preventDefault();
  const editId = document.getElementById('editGameId').value;
  const gameObj = {
    id: document.getElementById('gameId').value.trim(),
    title: document.getElementById('gameTitle').value.trim(),
    description: document.getElementById('gameDesc').value.trim(),
    category: document.getElementById('gameCategory').value,
    type: document.getElementById('gameType').value,
    status: document.getElementById('gameStatus').value,
    orientation: document.getElementById('gameOrientation').value,
    assetPath: document.getElementById('gameAssetPath').value.trim(),
    gameUrl: document.getElementById('gameUrl').value.trim(),
    thumbnailUrl: document.getElementById('gameThumb').value.trim(),
    rating: parseFloat(document.getElementById('gameRating').value) || 4.5,
    isFeatured: document.getElementById('gameFeatured').value === 'true',
    isPopular: document.getElementById('gamePopular').value === 'true',
    version: 1,
    playsCount: 1000
  };

  if (editId) {
    const idx = catalog.games.findIndex(g => g.id === editId);
    if (idx !== -1) catalog.games[idx] = { ...catalog.games[idx], ...gameObj };
    showToast(`Game '${gameObj.title}' updated successfully!`);
  } else {
    catalog.games.unshift(gameObj);
    showToast(`New game '${gameObj.title}' added successfully!`);
  }

  closeModal();
  saveCatalog();
}

function deleteGame(id) {
  const game = catalog.games.find(g => g.id === id);
  const name = game ? game.title : id;
  if (confirm(`Are you sure you want to delete game '${name}' from the admin catalog?`)) {
    catalog.games = catalog.games.filter(g => g.id !== id);
    saveCatalog();
    showToast(`Game '${name}' deleted`, 'error');
  }
}

function renderCategories() {
  const catGrid = document.getElementById('categoriesGrid');
  if (!catGrid || !catalog) return;
  catGrid.innerHTML = '';

  const categories = catalog.categories || DEFAULT_CATALOG.categories;

  categories.forEach(cat => {
    const count = catalog.games.filter(g => g.category.toLowerCase() === cat.name.toLowerCase()).length;
    const card = document.createElement('div');
    card.className = 'stat-card';
    card.innerHTML = `
      <div class="stat-icon" style="background: rgba(168, 85, 247, 0.15); color: var(--neon-purple);">${cat.icon || '🎮'}</div>
      <div class="stat-info" style="flex:1;">
        <h3>${cat.name}</h3>
        <p>${count} Registered Games</p>
      </div>
      <button class="btn btn-secondary" style="padding:4px 8px; font-size:10px; margin-right:4px;" onclick="editCategoryPrompt('${cat.id}')">Edit</button>
      <button class="btn btn-danger" style="padding:4px 8px; font-size:10px;" onclick="deleteCategory('${cat.id}')">Delete</button>
    `;
    catGrid.appendChild(card);
  });
}

function populateCategorySelects() {
  const filterCat = document.getElementById('filterCategory');
  const gameCat = document.getElementById('gameCategory');
  const categories = (catalog && catalog.categories) ? catalog.categories : DEFAULT_CATALOG.categories;

  if (filterCat) {
    const currentVal = filterCat.value || 'all';
    filterCat.innerHTML = '<option value="all">All Categories</option>';
    categories.forEach(c => {
      filterCat.innerHTML += `<option value="${c.name}">${c.name}</option>`;
    });
    filterCat.value = currentVal;
  }

  if (gameCat) {
    const currentVal = gameCat.value || (categories[0] ? categories[0].name : 'Arcade');
    gameCat.innerHTML = '';
    categories.forEach(c => {
      gameCat.innerHTML += `<option value="${c.name}">${c.name}</option>`;
    });
    gameCat.value = currentVal;
  }
}

function addCategoryForm(e) {
  if (e) e.preventDefault();
  const nameEl = document.getElementById('newCatName');
  const iconEl = document.getElementById('newCatIcon');

  const name = nameEl ? nameEl.value.trim() : '';
  const icon = (iconEl && iconEl.value.trim()) ? iconEl.value.trim() : '🎮';

  if (!name) return;

  if (!catalog.categories) catalog.categories = [...DEFAULT_CATALOG.categories];

  if (catalog.categories.some(c => c.name.toLowerCase() === name.toLowerCase())) {
    showToast(`Category '${name}' already exists!`, 'error');
    return;
  }

  catalog.categories.push({ id: name, name, icon });
  if (nameEl) nameEl.value = '';
  if (iconEl) iconEl.value = '';
  saveCatalog();
  showToast(`Category '${name}' added!`);
}

function editCategoryPrompt(catId) {
  if (!catalog.categories) return;
  const cat = catalog.categories.find(c => c.id === catId || c.name === catId);
  if (!cat) return;

  const newName = prompt(`Edit category name:`, cat.name);
  if (newName && newName.trim() !== "") {
    cat.name = newName.trim();
    cat.id = newName.trim();
    saveCatalog();
    showToast(`Category updated to '${cat.name}'`);
  }
}

function deleteCategory(catId) {
  if (!catalog.categories) return;
  const cat = catalog.categories.find(c => c.id === catId || c.name === catId);
  const name = cat ? cat.name : catId;

  if (confirm(`Delete category '${name}'? Registered games under this category will remain.`)) {
    catalog.categories = catalog.categories.filter(c => c.id !== catId && c.name !== catId);
    saveCatalog();
    showToast(`Category '${name}' removed`, 'error');
  }
}

function exportJson() {
  if (!catalog) return;

  // Filter ONLY published games for the public Android games.json catalog
  const publishedGames = catalog.games.filter(g => (g.status || 'published') === 'published');

  const exportObj = {
    version: catalog.version || 1,
    lastUpdated: new Date().toISOString().split('T')[0],
    games: publishedGames
  };

  const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(exportObj, null, 2));
  const anchor = document.createElement('a');
  anchor.setAttribute("href", dataStr);
  anchor.setAttribute("download", "games.json");
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  showToast(`Exported games.json with ${publishedGames.length} published games!`);
}

async function testGitHubConnection() {
  const owner = document.getElementById('ghOwner').value.trim();
  const repo = document.getElementById('ghRepo').value.trim();
  const branch = document.getElementById('ghBranch').value.trim() || 'main';
  const path = document.getElementById('ghPath').value.trim() || 'games.json';
  const token = document.getElementById('ghToken').value.trim();

  const consoleEl = document.getElementById('publishConsole');
  if (consoleEl) {
    consoleEl.style.display = 'block';
    consoleEl.innerText = '🔍 Testing GitHub REST API connectivity...';
  }

  if (!owner || !repo) {
    showToast('Please specify GitHub Owner and Repository Name', 'error');
    if (consoleEl) consoleEl.innerText = '❌ Missing GitHub Owner or Repository Name.';
    return;
  }

  try {
    const url = `https://api.github.com/repos/${owner}/${repo}/contents/${path}?ref=${branch}`;
    const headers = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(url, { headers });
    if (res.ok) {
      const data = await res.json();
      if (consoleEl) {
        consoleEl.innerText = `✅ GitHub connection successful!\nTarget Repository: ${owner}/${repo} (${branch})\nTarget File: ${path}\nFile SHA: ${data.sha}\nFile Size: ${data.size} bytes`;
      }
      showToast('GitHub repository access verified successfully!');
    } else {
      const errText = await res.text();
      if (consoleEl) {
        consoleEl.innerText = `❌ GitHub API returned HTTP ${res.status}:\n${errText}`;
      }
      showToast(`GitHub API returned HTTP ${res.status}`, 'error');
    }
  } catch (e) {
    if (consoleEl) consoleEl.innerText = `❌ Connection error: ${e.message}`;
    showToast(`Failed to connect to GitHub: ${e.message}`, 'error');
  }
}

async function publishToGitHub(e) {
  if (e) e.preventDefault();

  const owner = document.getElementById('ghOwner').value.trim();
  const repo = document.getElementById('ghRepo').value.trim();
  const branch = document.getElementById('ghBranch').value.trim() || 'main';
  const path = document.getElementById('ghPath').value.trim() || 'games.json';
  const token = document.getElementById('ghToken').value.trim();

  const consoleEl = document.getElementById('publishConsole');
  if (consoleEl) {
    consoleEl.style.display = 'block';
    consoleEl.innerText = '🚀 Initializing GitHub Catalog Publishing...';
  }

  if (!owner || !repo || !token) {
    showToast('Please provide GitHub Owner, Repo Name, and Personal Access Token (PAT)', 'error');
    if (consoleEl) consoleEl.innerText = '❌ Missing Owner, Repo Name, or Personal Access Token (PAT).';
    return;
  }

  try {
    localStorage.setItem('gh_owner', owner);
    localStorage.setItem('gh_repo', repo);
    localStorage.setItem('gh_branch', branch);
    localStorage.setItem('gh_path', path);

    // Step 1: Check existing file SHA from GitHub
    if (consoleEl) consoleEl.innerText = `1/3 Fetching current SHA for '${path}' from ${owner}/${repo}...`;
    const checkUrl = `https://api.github.com/repos/${owner}/${repo}/contents/${path}?ref=${branch}`;
    const checkRes = await fetch(checkUrl, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'application/vnd.github.v3+json'
      }
    });

    let existingSha = null;
    if (checkRes.ok) {
      const checkData = await checkRes.json();
      existingSha = checkData.sha;
    }

    // Step 2: Prepare published games payload (excluding draft entries)
    if (consoleEl) consoleEl.innerText = '2/3 Encoding catalog payload (excluding draft entries)...';
    const publishedGames = catalog.games.filter(g => (g.status || 'published') === 'published');
    const exportObj = {
      version: (catalog.version || 1) + 1,
      lastUpdated: new Date().toISOString().split('T')[0],
      games: publishedGames
    };

    const jsonString = JSON.stringify(exportObj, null, 2);
    const base64Content = btoa(unescape(encodeURIComponent(jsonString)));

    // Step 3: Put contents via GitHub REST API
    if (consoleEl) consoleEl.innerText = `3/3 Committing updated games.json to GitHub (${publishedGames.length} published games)...`;
    const putUrl = `https://api.github.com/repos/${owner}/${repo}/contents/${path}`;
    const bodyObj = {
      message: `Publish catalog update v${exportObj.version} via Devil Game Hub Admin Panel`,
      content: base64Content,
      branch: branch
    };
    if (existingSha) {
      bodyObj.sha = existingSha;
    }

    const putRes = await fetch(putUrl, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Accept': 'application/vnd.github.v3+json'
      },
      body: JSON.stringify(bodyObj)
    });

    if (putRes.ok) {
      const putData = await putRes.json();
      catalog.version = exportObj.version;
      saveCatalog();

      const rawUrl = `https://raw.githubusercontent.com/${owner}/${repo}/${branch}/${path}`;
      if (consoleEl) {
        consoleEl.innerText = `✅ PUBLISHED SUCCESSFULLY TO GITHUB!\nCommit SHA: ${putData.commit.sha}\nPublished Games: ${publishedGames.length}\nRaw URL for Android App: ${rawUrl}`;
      }
      showToast(`Catalog v${exportObj.version} published to GitHub successfully!`);
    } else {
      const errText = await putRes.text();
      if (consoleEl) consoleEl.innerText = `❌ GitHub Publish Failed (HTTP ${putRes.status}):\n${errText}`;
      showToast(`GitHub Publish Failed (HTTP ${putRes.status})`, 'error');
    }
  } catch (err) {
    if (consoleEl) consoleEl.innerText = `❌ Error publishing catalog: ${err.message}`;
    showToast(`Error publishing to GitHub: ${err.message}`, 'error');
  }
}

function resetDemoData() {
  if (confirm("Reset catalog to sample data? All local changes will be replaced.")) {
    localStorage.removeItem('devil_admin_catalog');
    catalog = JSON.parse(JSON.stringify(DEFAULT_CATALOG));
    saveCatalog();
    showToast("Catalog reset to default sample data");
  }
}

function clearLocalStorageData() {
  if (confirm("Clear all admin LocalStorage data? The dashboard will re-initialize with sample data.")) {
    localStorage.removeItem('devil_admin_catalog');
    catalog = JSON.parse(JSON.stringify(DEFAULT_CATALOG));
    renderAllViews();
    showToast("LocalStorage cleared and reset", 'error');
  }
}

function updateStorageInfo() {
  if (!catalog) return;
  const jsonStr = JSON.stringify(catalog);
  const kb = (jsonStr.length / 1024).toFixed(2);
  setElemText('storageInfo', `Last Updated: ${catalog.lastUpdated || 'Today'} | LocalStorage Usage: ${kb} KB | Total Games: ${catalog.games.length}`);
}

// Global Window Explicit Function Binding for Inline Handlers
window.switchView = switchView;
window.openAddGameModal = openAddGameModal;
window.openEditGameModal = openEditGameModal;
window.closeModal = closeModal;
window.saveGameForm = saveGameForm;
window.deleteGame = deleteGame;
window.toggleGameStatus = toggleGameStatus;
window.addCategoryForm = addCategoryForm;
window.editCategoryPrompt = editCategoryPrompt;
window.deleteCategory = deleteCategory;
window.exportJson = exportJson;
window.resetDemoData = resetDemoData;
window.clearLocalStorageData = clearLocalStorageData;
window.handleAddGameNav = handleAddGameNav;
window.renderGamesTable = renderGamesTable;
window.testGitHubConnection = testGitHubConnection;
window.publishToGitHub = publishToGitHub;
