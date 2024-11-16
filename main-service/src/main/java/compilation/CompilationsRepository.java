package compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import compilation.model.Compilation;

@Repository
public interface CompilationsRepository extends JpaRepository<Long, Compilation> {
}
