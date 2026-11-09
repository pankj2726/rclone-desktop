#!/usr/bin/env bash
# device-tunnel.sh — connect this sandbox to the remote Android device.
#
# The SSH password is NEVER passed on the command line (visible in ps) and never
# printed. Supply it one of two ways:
#
#     DEVICE_PASS='...' ./scripts/device-tunnel.sh          # env var
#     ./scripts/device-tunnel.sh                            # prompts, hidden input
#
# Everything transient lives in $TMPD (0700) and dies with the machine.
set -euo pipefail

HOST="${DEVICE_HOST:-7705858794031061803_1794160499@adb.foxphone.com}"
PORT="${DEVICE_PORT:-1824}"
LPORT=9536
TMPD="$(mktemp -d /tmp/devtun.XXXXXX)"
chmod 700 "$TMPD"
SSHOPTS=(-o StrictHostKeyChecking=accept-new -o HostKeyAlgorithms=+ssh-rsa
         -o PreferredAuthentications=password -o NumberOfPasswordPrompts=1
         -o ConnectTimeout=20)

cleanup() { rm -rf "$TMPD"; }
trap cleanup EXIT

if [ -z "${DEVICE_PASS:-}" ]; then
  if [ -t 0 ]; then
    read -rsp "device SSH password (hidden): " DEVICE_PASS; echo
  else
    echo "ERROR: DEVICE_PASS not set and no terminal to prompt on." >&2
    echo "       Re-run as: DEVICE_PASS='...' $0" >&2
    exit 64
  fi
fi
[ -n "$DEVICE_PASS" ] || { echo "ERROR: empty password." >&2; exit 64; }
export DEVICE_PASS

# askpass helper: prints the password from the environment, nothing on disk
cat >"$TMPD/askpass.sh" <<'EOF'
#!/bin/sh
printf '%s\n' "$DEVICE_PASS"
EOF
chmod 700 "$TMPD/askpass.sh"

echo "1/3 opening SSH tunnel 127.0.0.1:$LPORT -> foxphone:100 ..."
setsid env SSH_ASKPASS="$TMPD/askpass.sh" SSH_ASKPASS_REQUIRE=force DISPLAY=:0 \
  ssh "${SSHOPTS[@]}" -p "$PORT" -L "$LPORT:foxphone:100" -Nf "$HOST" || {
    echo "ERROR: tunnel failed (wrong password, or gateway down)." >&2; exit 2; }

for i in $(seq 1 20); do
  timeout 2 bash -c "cat < /dev/null > /dev/tcp/127.0.0.1/$LPORT" 2>/dev/null && break
  sleep 1
done
timeout 2 bash -c "cat < /dev/null > /dev/tcp/127.0.0.1/$LPORT" 2>/dev/null \
  || { echo "ERROR: tunnel did not come up." >&2; exit 3; }
echo "    tunnel up."

echo "2/3 adb connect ..."
adb connect "127.0.0.1:$LPORT" >/dev/null

echo "3/3 device identity ..."
adb -s "127.0.0.1:$LPORT" shell getprop ro.product.model 2>/dev/null | tr -d '\r' | sed 's/^/    model: /'
adb -s "127.0.0.1:$LPORT" shell getprop ro.build.version.release 2>/dev/null | tr -d '\r' | sed 's/^/    android: /'
echo "    ok — device address: 127.0.0.1:$LPORT"
