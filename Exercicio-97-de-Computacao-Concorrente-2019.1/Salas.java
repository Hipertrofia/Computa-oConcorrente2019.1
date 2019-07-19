import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Salas {
    int nSalas;
    int ocupantesDeSala;
    Condition[] podeEntrar;
    Manipulador[] manipuladores;
    ReentrantLock lock;
    
    int salasEmUso;
    static final int todasDisponiveis = -1;
    static final int sinalDeSaida = -2;

    public Salas(int m) {
        nSalas = m;
        salasEmUso = todasDisponiveis;
        ocupantesDeSala = 0;
        lock = new ReentrantLock(true);
        podeEntrar = new Condition[m];
        manipuladores = new SinalDeSaida[m];

        for (int i = 0; i < m; i++) {
            podeEntrar[i] = lock.newCondition();
            definicaoDoSinalDeSaida(i, new SinalDeSaida(i));
        }
    }

    public void entrar(int i) {
        lock.lock();
        try {
            while (salasEmUso != i && salasEmUso != todasDisponiveis) {
                podeEntrar[i].await();
            }
            salasEmUso = i;
            ocupantesDeSala++;
        } catch (Interrupcao e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public boolean sair() {
        lock.lock();
        try {
            ocupantesDeSala--;
            if (ocupantesDeSala == 0) {
                salasEmUso = sinalDeSaida;
                return true;
            }
        }finally {
            lock.unlock();
        }
        return false;
    }

    public void chamadaDoSinalDeSaida(int i) {
        manipuladores[i].Vazia();
    }


   public void definicaoDoSinalDeSaida(int i, Salas.Manipulador h) {
        manipuladores[i] = h;
    }

    public interface Manipulador {
        void Vazia();

    }

    private class SinalDeSaida implements Manipulador {
        int SalasNum;
        SinalDeSaida(int SalasNum) {
            this.SalasNum = SalasNum;
        }

        @Override
        public void Vazia() {
            lock.lock();
            try {
                salasEmUso = proximaSalasLivres();
                if (salasEmUso >= 0) {
                    podeEntrar[salasEmUso].signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        private int proximaSalasLivres() {
            for (int i = 0; i < nSalas; i++) {
                int candidato = (nSalas + i + 1) % nSalas;
                if (lock.hasWaiters(podeEntrar[candidato])) {
                    return candidato;
                }
            }
            return todasDisponiveis;
        }
    }
}
