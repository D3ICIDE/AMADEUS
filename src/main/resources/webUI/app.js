var rawMarkdown = "";
var contentDiv = document.getElementById('content');
var promptContainer = document.getElementById('prompt-container');
var promptTextDiv = document.getElementById('prompt-text');

function setPrompt(text) {
    promptTextDiv.textContent = text;
    promptContainer.style.display = "block";
}

function enhanceCodeBlocks() {
    var pres = contentDiv.querySelectorAll('pre[class*="language-"]');

    pres.forEach(function(pre) {
        // 1. Copy Button Logic
        if (!pre.parentElement.classList.contains('code-block')) {
            var wrapper = document.createElement('div');
            wrapper.className = 'code-block';
            pre.parentNode.insertBefore(wrapper, pre);
            wrapper.appendChild(pre);

            var btn = document.createElement('button');
            btn.className = 'code-copy-btn';
            btn.textContent = 'Copy';

            btn.addEventListener('click', function() {
                var code = pre.querySelector('code');
                var text = code ? code.textContent : pre.textContent;
                if (navigator.clipboard && navigator.clipboard.writeText) {
                    navigator.clipboard.writeText(text).then(function () {
                        btn.textContent = 'Copied';
                        setTimeout(function () { btn.textContent = 'Copy'; }, 1000);
                    }, function () { btn.textContent = 'Failed'; setTimeout(function () { btn.textContent = 'Copy'; }, 1000); });
                } else {
                    var range = document.createRange(); range.selectNodeContents(pre);
                    var sel = window.getSelection(); sel.removeAllRanges(); sel.addRange(range);
                    document.execCommand('copy'); btn.textContent = 'Copied';
                    setTimeout(function () { btn.textContent = 'Copy'; }, 1000);
                    sel.removeAllRanges();
                }
            });
            wrapper.appendChild(btn);
        }

        // 2. Scroll Sync for Line Numbers
        pre.onscroll = function() {
            var rows = pre.querySelector('.line-numbers-rows');
            if(rows) {
                rows.style.transform = 'translateX(' + pre.scrollLeft + 'px)';
            }
        };
    });
}

function appendContent(chunk) {
    rawMarkdown += chunk;
    contentDiv.innerHTML = marked.parse(rawMarkdown);

    contentDiv.querySelectorAll('pre').forEach(function(pre) {
        if (pre.querySelector('code')) {
            pre.classList.add('line-numbers');
        }
    });

    Prism.highlightAllUnder(contentDiv);
    enhanceCodeBlocks();

    window.scrollTo(0, document.body.scrollHeight);
}

function resetChat() {
    contentDiv.innerHTML = "";
    rawMarkdown = "";
    promptTextDiv.textContent = "";
    promptContainer.style.display = "none";
}